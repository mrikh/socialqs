const mongoose = require('mongoose')
const constants = require('../utils/constants')
const validator = require('validator')

const answerSchema = new mongoose.Schema({
    creator : {
        type : mongoose.Schema.Types.ObjectId,
        required : true,
        ref : 'User'
    },
    questionId : {
        type : mongoose.Schema.Types.ObjectId,
        required : true,
        ref : 'Question'
    },
    videoUrl : {
        type : String,
        required : true,
        validate(value){
            if (!validator.isURL(value)){
                throw new Error(constants.invalid_url)
            }
        }
    },
    thumbnailUrl : {
        type : String,
        required : true,
        validate(value){
            if (!validator.isURL(value)){
                throw new Error(constants.invalid_url)
            }
        }
    },
    likes : [{
        type : mongoose.Schema.Types.ObjectId,
        required : true,
        ref : 'User'
    }],
    dislikes: [{
        type : mongoose.Schema.Types.ObjectId,
        required : true,
        ref : 'User'
    }],
    isCorrect : {
        type : Boolean,
        default : false
    }
},{
    timestamps : true
})

answerSchema.methods.toJSON = function() {

    const answerObject = this.toObject()
    delete answerObject.__v
    delete answerObject.updatedAt
    delete answerObject.questionId
    answerObject.createdAt = answerObject.createdAt.getTime()
    return answerObject
}


const Answer = mongoose.model('Answer', answerSchema)
module.exports = Answer