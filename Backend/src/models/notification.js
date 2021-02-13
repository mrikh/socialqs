const mongoose = require('mongoose')

const notificationSchema = new mongoose.Schema({
    users :[{
        type : mongoose.Schema.Types.ObjectId,
        ref : 'User',
        required : true
    }],
    questionId : {
        type : mongoose.Schema.Types.ObjectId,
        ref : 'Question',
        required : true
    },
    title : {
        type : String,
        required : true
    },
    body: {
        type : String,
        required : true 
    }
},{
    timestamps : true
})

notificationSchema.methods.toJSON = function() {

    const notificationObject = this.toObject()
    delete notificationObject.__v
    delete notificationObject.users
    notificationObject.createdAt = notificationObject.createdAt.getTime()
    delete notificationObject.updatedAt
    return notificationObject
}

const Notification = mongoose.model('Notification', notificationSchema)
module.exports = Notification