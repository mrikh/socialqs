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

        const question = new Question(req.body)
        await question.save()

        const user = req.user
        user.createdQuestions.push(question._id)
        await user.save()

        await question.populate({
            path : 'creator',
            options : { select : { _id : 1, name : 1, profilePhoto : 1 }}
        }).populate({
            path : 'category',
            options : { select : { _id : 1, name : 1}} 
        }).execPopulate()

        var questionJson = question.toJSON()
        questionJson.answerCount = 0
        return res.status(200).send({code : 200, message : constants.success, data : questionJson})
    }catch(error){
        next(error)
    }
})

router.get('/questions/list', auth, async (req, res, next) => {
    try{
        const categoryId = req.query.categoryId

        if (!categoryId){
            const error = new Error(constants.params_missing)
            error.statusCode = 400
            throw error
        }

        const results = await Question.aggregate([
            {
                $match:{
                    $and : [
                        { category : mongoose.Types.ObjectId(categoryId) },
                        { category : {$ne : null} }
                    ]
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
                        {$project : {name : 1, profilePhoto : 1}}
                    ]
                }
            }, {$unwind: "$creator"},{
                $lookup: {
                    from: "categories",
                    let : { id: "$category" },
                    as : "category",
                    pipeline: [
                        { $match : { $expr: { $eq: ["$_id", "$$id"] } }},
                        { $project: { name: 1}}
                    ]
                }
            }, {$unwind : "$category"},{
                $unset: ["createdAt", "updatedAt", "__v"]
            }
        ])

        return res.status(200).send({code : 200, message : constants.success, data : { 'result':results}})
    }catch(error){
        next(error)
    }
})


module.exports = router