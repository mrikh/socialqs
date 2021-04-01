const express = require('express')
const auth = require('../middleware/auth')
const User = require('../models/user.js')
const Answer = require('../models/answer.js')
const constants = require('../utils/constants.js')
const Notification = require('../models/notification.js')
const admin = require('../utils/firebase.js')

const router = new express.Router()

router.post('/answers/answer', auth, async (req, res, next) => {

    try{
        var params = req.body
        params.creator = req.user._id

        const answer = new Answer(params)
        
        if(!answer){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }
        
        await answer.save()        
        await answer.populate({
            path: 'creator',
            options: { select: { _id: 1, name: 1, profilePhoto: 1 } }
        }).populate({
            path: 'questionId',
            options: { select: { creator: 1, _id: 1, title: 1 } }
        }).execPopulate()
        
        if (!req.user._id.equals(answer.questionId.creator._id)){
            //create notification
            const message = constants.new_answer_body + " " + answer.questionId.title + " " + constants.by + " " + req.user.name
            const title = constants.new_answer_title
            const notification = new Notification({
                users : [answer.questionId.creator], 
                questionId : answer.questionId, 
                title : title,
                body: message
            })

            await notification.save()   

            const creatorUser = await User.findById(answer.questionId.creator._id)
            if (creatorUser.token && creatorUser.token != ''){
                const pushMessage = {
                    notification : {
                        title : title,
                        body : message
                    },
                    token : creatorUser.pushToken
                }
        
                admin.messaging().send(pushMessage).then( response => {
                    console.log("Notification sent successfully")
                }).catch( error => {
                    console.log(error);
                });   
            }
        }
        
        return res.status(200).send({code : 200, message : constants.success, data : answer})

    }catch(error){
        next(error)
    }
})

router.get('/answers/list', auth, async (req, res, next) => {

    try{
        const questionId = req.query.questionId

        if (!questionId){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        const results = await Answer.find({questionId : questionId}).sort({updatedAt : -1}).populate({
            path: 'creator',
            options : { select : { _id : 1, name : 1, profilePhoto : 1 , blockedUsers : 1}}
        })

         //dont show posts of users you are blocking
        var finalJson = results.filter((answer) => {
            if (req.user.blockedUsers.includes(answer.creator._id)){
                return false
            }else{
                return true
            }
        })
        //dont show posts of users who have blocked you
        finalJson = finalJson.filter((answer) => {
            const isInArray = answer.creator.blockedUsers.some((user) => {
                return user.equals(req.user._id);
            });
            delete answer.creator.blockedUsers
            if (isInArray){
                return false
            }else{
                return true
            }
        })
        
        return res.status(200).send({code : 200, message : constants.success, data : {'result' : finalJson}})

    }catch(error){
        next(error)
    }
})

router.patch('/answers/update', auth, async (req, res, next) => {

    try{
        const answerId = req.body.answerId
        const isCorrect = req.body.isCorrect
        const like = req.body.like
        const dislike = req.body.dislike

        if (!answerId){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        if ((isCorrect == null || isCorrect == undefined) && (like == null || 
            like == undefined) && (dislike == null || dislike == undefined)){
                const error = new Error(constants.params_missing)
                error.statusCode = 400
                throw error
            }

        const answer = await Answer.findById(answerId)

        if (!answer){   
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        if (isCorrect != null || isCorrect != undefined){
            answer.isCorrect = isCorrect
        }

        if (like != null || like != undefined){
            if (like){
                answer.likes.push(req.user._id)
                answer.dislikes.pull(req.user._id)
            }else{
                answer.likes.pull(req.user._id)
            }
        }

        if (dislike != null || dislike != undefined){
            if (dislike){
                answer.likes.pull(req.user._id)
                answer.dislikes.push(req.user._id)
            }else{
                answer.dislikes.pull(req.user._id)
            }
        }
        await answer.save()
        return res.send({code : 200, message : constants.success})
        
    }catch(error){
        next(error)
    }
})

router.delete('/answers/:id', auth, async (req, res, next) => {
    try{
        const answerId = req.params.id
        
        if (!answerId){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        await Answer.deleteOne({_id : answerId})
        return res.send({code : 200, message : constants.success})
    }catch(error){
        next(error)
    }
})

module.exports = router