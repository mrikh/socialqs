const express = require('express')
const User = require('../models/user')
const constants = require('../utils/constants')
const {sendVerificationMail, sendForgotMail} = require('../emails/account')
const auth = require('../middleware/auth')
const bcrypt = require('bcrypt')
const otpHandler = require('../utils/otp')

const router = new express.Router()

router.post('/users/signUp', async (req, res, next) => {
    try{
        const user = new User(req.body)
        const token = await user.generateAuthToken()
        sendVerificationMail(user)
        return res.send({code : 200, message : constants.success_signup, data : {user, token}})
    }catch (error){
        next(error)
    }
})

router.post('/users/login', async (req, res, next) => {
    try{
        const params = req.body

        if (!params.email || (!params.password && !params.socialId)){
            const error = new Error(constants.params_missing)
            error.statusCode = 422
            throw error
        }

        const user = await User.findOne({email : params.email})

        if (!user){
            if (params.socialId){
                const user = new User(req.body)
                const token = await user.generateAuthToken()
                return res.send({code : 200, message : constants.success_signup, data : {user, token}})                
            }else{
                const error = new Error(constants.user_not_found)
                error.statusCode = 404
                throw error
            }
        }else{
            if (params.password){
                const isMatch = await bcrypt.compare(params.password, user.password)
                if (isMatch){
                    const token = await user.generateAuthToken()
                    return res.send({code : 200, message : constants.success, data : {user, token}})
                }else{
                    const error = new Error(constants.params_missing)
                    error.statusCode = 404
                    throw error
                }
            }else if (params.socialId){
                //social id exists, different social id, social id wrong
                if (user.socialId === params.socialId){
                    const token = await user.generateAuthToken()
                    return res.send({code : 200, message : constants.success, data : {user, token}})
                }else if (!user.socialId){
                    //empty social id but user exists as he logged in from this email manually
                    if (user.emailVerified){
                        //email also verified. Update social id
                        user.socialId = parms.socialId
                        await user.save()
                        const token = await user.generateAuthToken()
                        return res.send({code : 200, message : constants.success, data : {user, token}})
                    }else{
                        //email not verified.
                        const error = new Error(constants.social_login_attempt_unverified_email)
                        error.statusCode = 404
                        throw error
                    }
                }else{
                    const error = new Error(constants.params_missing)
                    error.statusCode = 404
                    throw error
                }
            }else{
                const error = new Error(constants.params_missing)
                error.statusCode = 404
                throw error
            }
        }
    }catch(error){
        next(error)
    }
})

router.post('/users/resendVerification', async (req, res, next) => {
    try{
        const email = req.body.email
        const user = await User.findOne({email : email})
        const isForgotPassword = req.body.isForgot

        if (!user){
            return res.send({code : 404, message : constants.user_not_found})
        }else{

            if (isForgotPassword){
                sendForgotMail(email)
                return res.send({code : 200, message : constants.forgot_success})
            }else{
                sendVerificationMail(user)
                return res.send({code : 200, message : constants.success_signup})
            }
        }
    }catch (error){
        next(error)
    }
})

router.post('/users/verifyEmail', async (req, res, next) => {
    try{
        const email = req.body.email
        const user = await User.findOne({email: email})

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

router.post('/users/forgotPass', async (req, res, next) => {

    try{
        const email = req.body.email
        const user = await User.findOne({email : email})
        if (!user){
            const error = new Error(constants.user_not_found)    
            error.statusCode = 404
            throw error
        }
        sendForgotMail(user)
        res.send({code : 200, message : constants.forgot_success})
    }catch (error){
        next(error)
    }
})

module.exports = router