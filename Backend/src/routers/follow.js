const express = require('express')
const auth = require('../middleware/auth')
const Follow = require('../models/follow.js')
const mongoose = require('mongoose')
const constants = require('../utils/constants.js')

const router = new express.Router()

router.get('/follow/list', auth, async (req, res, next) => {

    try{
        const userId = req.user._id
        
        const result = await Follow.findOne({userId : userId}).populate({
            path : 'followers',
            options : { select : { _id : 1, name : 1, profilePhoto : 1 }}
        }).populate({
            path: 'following',
            options : { select : { _id : 1, name : 1, profilePhoto : 1 }}
        })

        if (!result){
            return res.status(200).send({code : 200, message : constants.success, data : {'followers' : [], 'following': []}})
        }else{
            return res.status(200).send({code : 200, message : constants.success, data : {'followers' : result.followers, 'following': result.following}})
        }

    }catch(error){
        next(error)
    }
})

router.post('/follow', auth, async (req, res, next) => {

    try{
        
        const currentUserId = req.user._id
        const toFollowUserId = req.body.id
        const follow = req.body.follow

        if (!toFollowUserId || !currentUserId || follow == undefined){   
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        if (toFollowUserId === currentUserId){   
            const error = new Error(constants.invalid_follower_following)
            error.statusCode = 400
            throw error
        }

        var toFollowObject = await Follow.findOne({userId : toFollowUserId})
        if (!toFollowObject){
            //create
            toFollowObject = new Follow()
            toFollowObject.userId = mongoose.Types.ObjectId(toFollowUserId)
        }

        if (follow){
            toFollowObject.followers.push(currentUserId)
        }else{
            toFollowObject.followers.pull(currentUserId)
        }
        await toFollowObject.save()

        var fromFollowObject = await Follow.findOne({userId : currentUserId})

        if (!fromFollowObject){
            //create
            fromFollowObject = new Follow()
            fromFollowObject.userId = mongoose.Types.ObjectId(currentUserId)
        }

        if (follow){
            fromFollowObject.following.push(toFollowUserId)
        }else{
            fromFollowObject.following.pull(toFollowUserId)
        }
        await fromFollowObject.save()

        return res.status(200).send({code : 200, message : constants.success})
    }catch(error){
        next(error)
    }
})

module.exports = router