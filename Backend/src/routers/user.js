const express = require('express')
const User = require('../models/user')
const constants = require('../utils/constants')
const {sendVerificationMail, sendForgotMail} = require('../emails/account')
const auth = require('../middleware/auth')
const bcrypt = require('bcrypt')
const speakeasy = require('speakeasy')
const otpHandler = require('../utils/otp')

const router = new express.Router()

router.post('/users/signUp', async (req, res, next) => {
    try{
        const user = new User(req.body)
        const token = await user.generateAuthToken()
        sendVerificationMail(user)
        res.send({code : 200, message : constants.success_signup, data : {user, token}})
    }catch (error){
        next(error)
    }
})

router.post('/users/resendVerification', auth, async (req, res, next) => {
    try{
        const user = req.user
        if (!user){
            res.send({code : 404, message : constants.user_not_found})
        }else{
            sendVerificationMail(user)
            res.send({code : 200, message : constants.success_signup})
        }
    }catch (error){
        next(error)
    }
})

router.post('/users/verifyEmail', auth, async (req, res, next) => {
    try{
        const user = req.user

        if (!user){
            res.send({code : 404, message : constants.user_not_found})
        }else{
    
            const tokenValidates = otpHandler.verifyOtp(user, req.body.otp)

            try{
                if (tokenValidates){
                    user.emailVerified = true
                    await user.save()
                    res.send({code : 200, message: constants.verification_success, data : {user}})
                }else{
                    res.send({code : 404, message : constants.verification_failed})
                }
            }catch(error){
                next(error)
            }
        }
    }catch (error){
        next(error)
    }
})

router.post('/users/login', async (req, res, next) => {
    try{
        const params = req.body
        
        if (!params.email || !params.password){
            const error = new Error(constants.params_missing)
            error.statusCode = 422
            throw error
        }
        
        const user = await User.findOne({email : params.email})
        
        if (!user){
            const error = new Error(constants.user_not_found)
            error.statusCode = 404
            throw error
        }

        const isMatch = await bcrypt.compare(params.password, user.password)

        if (isMatch){
            const token = await user.generateAuthToken()
            return res.send({code : 200, message : constants.success, data : {user, token}})
        }else{
            const error = new Error(constants.params_missing)
            error.statusCode = 404
            throw error
        }

    }catch(error){
        next(error)
    }
})

router.post('/users/forgotPass', async (req, res, next) => {

    try{
        const email = req.body.email
        const user = await User.findOne({email : email})

        if (!user){
            const error = new Error(constants.user_not_found)    
            error.statusCode = 404
            throw error
        }

        sendForgotMail(email)
        res.send({code : 200, message : constants.forgot_success})
    }catch (error){
        next(error)
    }
})

router.post('/users/sendOTP', auth, async (req, res, next) => {

    const token = otpHandler.generateOtp(req.user)
    const phoneNumber = req.body.phoneNumber

    try{
        res.send({code : 200, message : constants.success, data: {otp : token}})  
    }catch(error){
        next(error)
    }
})

router.post('/users/verifyOTP', auth, async (req, res, next) => {
    
    const tokenValidates = otpHandler.verifyOtp(req.user, req.body.token)
    const user = req.user
    
    try{
        if (tokenValidates){
            user.phoneVerified = true
            await user.save()
            res.send({code : 200, message: constants.verification_success, data : {user}})
        }else{
            res.send({code : 404, message : constants.verification_failed})
        }
    }catch(error){
        next(error)
    }
})

module.exports = router