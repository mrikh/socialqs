const express = require('express')
const User = require('../models/user')
const Follow = require('../models/follow')
const constants = require('../utils/constants')
const mongoose = require('mongoose')
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

router.post('/users/resetPassword', async (req, res, next) => {

    try{
        const email = req.body.email
        const user = await User.findOne({email : email})

        if (!user){
            const error = new Error(constants.user_not_found)    
            error.statusCode = 404
            throw error
        }
        
        const newPass = req.body.password
        user.password = newPass
        await user.save()
        return res.send({code : 200, message : constants.success})
    }catch (error){
        next(error)
    }
})

router.post('/users/updatePassword', async (req, res, next) => {

    try{
        const email = req.body.email
        const user = await User.findOne({email : email})

        if (!user){
            const error = new Error(constants.user_not_found)    
            error.statusCode = 404
            throw error
        }
        
        const oldPass = req.body.oldPassword
        const newPass = req.body.password

        const isMatch = await bcrypt.compare(oldPass, user.password)

        if (isMatch){
            user.password = newPass
            await user.save()
            return res.send({code : 200, message : constants.success})
        }else{
            const error = new Error(constants.password_not_match)    
            error.statusCode = 404
            throw error
        }
    }catch (error){
        next(error)
    }
})

router.get('/users/search', auth, async (req, res, next) => {

    try{
        const searchString = req.query.search

        if (!searchString){   
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        const results = await User.aggregate([
            {
                $match: {
                    name : {
                        $regex: searchString,
                        $options : 'i'
                    }
                }                
            },{
                $project : {
                    name : 1,
                    profilePhoto : 1,
                    _id : 1,
                    blockedUsers : 1
                }
            }
        ])

        //dont show posts of users you are blocking
        var finalJson = results.filter((user) => {
            if (req.user.blockedUsers.includes(user._id)){
                return false
            }else{
                return true
            }
        })
        
        //dont show posts of users who have blocked you
        finalJson = finalJson.filter((user) => {
            const isInArray = user.blockedUsers.some((temp) => {
                return temp.equals(req.user._id);
            });
            delete user.blockedUsers
            if (isInArray){
                return false
            }else{
                return true
            }
        })

        return res.status(200).send({code : 200, message : constants.success, data : {'results' : finalJson}})
    }catch(error){
        next(error)
    }
})

router.get('/users/details', auth, async (req, res, next) => {

    try{
        const id = req.query.id

        if (!id){   
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        var aggregate = [{
            $match: {
                _id : mongoose.Types.ObjectId(id)
            }   
        },{
            $lookup: {
                from: "questions",
                as: "created",
                let: { currentId: "$_id" },
                pipeline: [
                    { $match: { $expr: { $eq: ["$$currentId", "$creator"] } } },
                    {$unset : ["bookmarkedBy", "createdAt", "updatedAt", "__v", "creator", "blockedUsers"]}],
            }
        },{
            $unset: ["createdAt", "updatedAt", "__v", "password", "socialId", "email", "emailVerified", "token", "blockedUsers"]
        }]

        if (id == req.user._id){
            aggregate.push({
                $lookup: {
                    from: "questions",
                    as : "bookmarked",
                    let : {currentId : "$_id"},
                    pipeline : [
                        {$match : {$expr : {$in: ["$$currentId","$bookmarkedBy"]}}},
                        {$unset : ["bookmarkedBy", "createdAt", "updatedAt", "__v", "creator", "blockedUsers"]}
                    ]
                }
            })
        }

        const results = await User.aggregate(aggregate)
        return res.status(200).send({code : 200, message : constants.success, data : {'results' : results}})
    }catch(error){
        next(error)
    }
})

router.patch('/users/updateInfo', auth, async (req, res, next) => {

    const updates = Object.keys(req.body)
    const allowedUpdates = ['name', 'profilePhoto']
    const isValid = updates.every((update) => allowedUpdates.includes(update))

    try{
        if (!isValid){
            const error = new Error(constants.invalid_updates)
            error.statusCode = 400
            throw error
        }

        const user = req.user
        updates.forEach((update) => {
            user[update] = req.body[update]
        })
        await user.save()
        res.send({code : 200, message : constants.success, data : user})
        
    }catch(error){
        next(error)
    }
})

router.patch('/users/block', auth, async (req, res, next) => {

    try{
        const userToBlock = req.body.userId
        const currentUser = req.user
        const block = req.body.block

        if (!userToBlock || userToBlock === currentUser._id || block == undefined){   
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        if (block){
            req.user.blockedUsers.push(userToBlock)
        }else{
            req.user.blockedUsers.pull(userToBlock)
        }

        await req.user.save()

        //remove from follower and following listing as well
        const mineFollowObject = await Follow.findOne({userId : req.user._id})
        if (mineFollowObject){
            mineFollowObject.followers.pull(userToBlock)
            mineFollowObject.following.pull(userToBlock)
        }
        await mineFollowObject.save()

        const hisFollowObject = await Follow.findOne({userId : req.user._id})
        if (hisFollowObject){
            hisFollowObject.followers.pull(req.user._id)
            hisFollowObject.following.pull(req.user._id)
        }

        await hisFollowObject.save()
        return res.status(200).send({code : 200, message : constants.success})
        
    }catch(error){
        next(error)
    }
})

router.get('/users/block', auth, async (req, res, next) => {

    try{
        const currentUser = req.user

        await currentUser.populate({
            path : 'blockedUsers',
            options : { select : { _id : 1, name : 1, profilePhoto : 1 }}
        }).execPopulate()

        return res.status(200).send({code : 200, message : constants.success, data : {'results' : currentUser.blockedUsers}})
    }catch(error){
        next(error)
    }
})

module.exports = router