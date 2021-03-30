const express = require('express')
const mongoose = require('mongoose')
const Category = require('../models/category.js')
const auth = require('../middleware/auth')
const Question = require('../models/question.js')
const constants = require('../utils/constants.js')

const router = new express.Router()

router.post('/questions/category', auth, async (req, res, next) => {
    try{
        const category = new Category(req.body)
        await category.save()
        return res.send({code : 200, message : constants.category_added, data : {category}})
    }catch (error){
        next(error)
    }
})

router.get('/questions/category', auth, async (req, res, next) => {
    try{
        const categories = await Category.find()
        return res.send({code : 200, message : constants.category_added, data : {categories}})
    }catch (error){
        next(error)
    }
})

router.post('/questions/create', auth, async (req, res, next) => {
    try{
        var params = req.body
        params.creator = req.user._id

        const category = await Category.findById(params.category)
        if (!category){
            const error = new Error(constants.incorrect_category)
            error.statusCode = 400
            throw error
        }

        const question = new Question(req.body)
        await question.save()

        await question.populate({
            path : 'creator',
            options : { select : { _id : 1, name : 1, profilePhoto : 1 }}
        }).populate({
            path : 'category',
            options : { select : { _id : 1, name : 1}} 
        }).execPopulate()

        var questionJson = question.toJSON()
        questionJson.answerCount = 0
        delete questionJson.bookmarkedBy
        return res.status(200).send({code : 200, message : constants.success, data : questionJson})
    }catch(error){
        next(error)
    }
})

router.get('/questions/list', auth, async (req, res, next) => {
    try{
        const categoryId = req.query.categoryId
        const search = req.query.search

        var conditions = {}

        if (categoryId){
            conditions = {
                category : mongoose.Types.ObjectId(categoryId)
            }
        }else{
            conditions = {
                category : {$ne : null}
            }
        }

        if (search){
            conditions.title = {
                $regex: search,
                $options : 'i'
            }
        }
        
        const results = await Question.aggregate([
            {
                $match: conditions
                
            },{
                $lookup: {
                    from: "answers",
                    let: { questionId: "$_id" },
                    pipeline: [{ $match: { $expr: { $eq: ["$$questionId", "$questionId"] } } }],
                    as: "answerCount"
                }
            },{ $addFields: { answerCount: { $size: "$answerCount" }}}, {
                $lookup: {
                    from: "users",
                    let : {id : "$creator"},
                    as : "creator",
                    pipeline : [
                        {$match : {$expr : {$eq: ["$$id","$_id"]}}},
                        {$project : {name : 1, profilePhoto : 1, _id : 1, blockedUsers : 1}}
                    ]
                }
            }, {$unwind: "$creator"},{
                $lookup: {
                    from: "categories",
                    let : { id: "$category" },
                    as : "category",
                    pipeline: [
                        { $match : { $expr: { $eq: ["$_id", "$$id"] } }},
                        { $project: { name: 1, _id : 1}}
                    ]
                }
            }, 
            {$unwind : "$category"},
            { $sort : { createdAt : -1 } },
            { $unset: ["createdAt", "updatedAt", "__v"] }
        ])

        var finalJson = results.map((question) => {
            const temp = question
            if (req.user._id){
                temp.isBookmarked = question.bookmarkedBy.some((id) => {
                    return id.equals(req.user._id)
                })
            }else{
                temp.isBookmarked = false
            }

            delete temp.bookmarkedBy
            return temp
        })

        //dont show posts of users you are blocking
        finalJson = finalJson.filter((question) => {
            if (req.user.blockedUsers.includes(question.creator._id)){
                return false
            }else{
                return true
            }
        })
        //dont show posts of users who have blocked you
        finalJson = finalJson.filter((question) => {
            const isInArray = question.creator.blockedUsers.some((user) => {
                return user.equals(req.user._id);
            });
            delete question.creator.blockedUsers
            if (isInArray){
                return false
            }else{
                return true
            }
        })

        return res.status(200).send({code : 200, message : constants.success, data : { 'result' : finalJson}})
    }catch(error){
        next(error)
    }
})


router.get('/questions/details', auth, async (req, res, next) => {
    try{
        const questionId = req.query.id

        if (!questionId){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        const results = await Question.aggregate([
            {
                $match: {
                    _id : mongoose.Types.ObjectId(questionId)
                }
                
            },{
                $lookup: {
                    from: "answers",
                    let: { questionId: "$_id" },
                    pipeline: [{ $match: { $expr: { $eq: ["$$questionId", "$questionId"] } } }],
                    as: "answerCount"
                }
            },{ $addFields: { answerCount: { $size: "$answerCount" }}}, {
                $lookup: {
                    from: "users",
                    let : {id : "$creator"},
                    as : "creator",
                    pipeline : [
                        {$match : {$expr : {$eq: ["$$id","$_id"]}}},
                        {$project : {name : 1, profilePhoto : 1, _id : 1}}
                    ]
                }
            }, {$unwind: "$creator"},{
                $lookup: {
                    from: "categories",
                    let : { id: "$category" },
                    as : "category",
                    pipeline: [
                        { $match : { $expr: { $eq: ["$_id", "$$id"] } }},
                        { $project: { name: 1, _id : 1}}
                    ]
                }
            }, {$unwind : "$category"},{
                $unset: ["createdAt", "updatedAt", "__v"]
            }
        ])

        const finalJson = results.map((question) => {
            const temp = question
            if (req.user._id){
                temp.isBookmarked = question.bookmarkedBy.some((id) => {
                    return id.equals(req.user._id)
                })
            }else{
                temp.isBookmarked = false
            }

            delete temp.bookmarkedBy
            return temp
        })

        return res.status(200).send({code : 200, message : constants.success, data : { 'result':finalJson}})
    }catch(error){
        next(error)
    }
})


router.patch('/questions/bookmark', auth, async (req, res, next) => {
    try{
        const questionId = req.body.questionId
        const isBookmarked = req.body.isBookmarked
        
        if (!questionId || (isBookmarked == null || isBookmarked == undefined)){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        const question = await Question.findById(questionId)

        if (isBookmarked){
            question.bookmarkedBy.push(req.user._id)
        }else{
            question.bookmarkedBy.pull(req.user._id)
        }
        
        await question.save()
        return res.send({code : 200, message : constants.success})
        
    }catch(error){
        next(error)
    }
})

router.delete('/questions/:id', auth, async (req, res, next) => {
    try{
        const questionId = req.params.id
        
        if (!questionId){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        await Question.deleteOne({_id : questionId})
        return res.send({code : 200, message : constants.success})
    }catch(error){
        next(error)
    }
})

module.exports = router