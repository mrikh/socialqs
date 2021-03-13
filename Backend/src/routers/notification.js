const express = require('express')
const auth = require('../middleware/auth')
const Notification = require('../models/notification.js')
const constants = require('../utils/constants.js')
const mongoose = require('mongoose')

const router = new express.Router()

router.get('/notifications', auth, async (req, res, next) => {

    try{
        const user = req.user
        const result = await Notification.find({users : user._id})
        return res.status(200).send({code : 200, message : constants.success, data : {'result' : result}})

    }catch(error){
        next(error)
    }
})

router.delete('/notifications/id/:id', auth, async (req, res, next) => {

    console.log("___________")
    try{

        console.log("=========1==========")
        console.log(req)
        console.log("=========2==========")
        console.log(req.params)
        console.log("=========3==========")
        console.log(req.body)

        const notificationId = req.params.id
        const currentUserId = req.user._id
        
        if (!notificationId){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        const notif = await Notification.findById(notificationId)

        if (!notif) {
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        notif.users.pull(currentUserId)

        if (notif.users.length == 0){
            await Notification.deleteOne({_id : notificationId})
        }else{
            await notif.save()
        }
        return res.send({code : 200, message : constants.success})
    }catch(error){
        next(error)
    }
})

router.delete('/notifications/all', auth, async (req, res, next) => {

    try{
        
        const currentUserId = req.user._id
        const results = await Notification.find({ users : currentUserId})
        
        var idsToDelete = []
        
        results.forEach(async (result) => {
            result.users.pull(currentUserId)
            if (result.users.length == 0){
                idsToDelete.push(result._id)
            }else{
                await result.save()
            }
        })

        await Notification.deleteMany({_id : {$in: idsToDelete}})
        return res.send({code : 200, message : constants.success})
    }catch(error){
        next(error)
    }
})

module.exports = router