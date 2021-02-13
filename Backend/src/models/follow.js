const mongoose = require('mongoose')

const followSchema = new mongoose.Schema({
    userId : {
        type : mongoose.Schema.Types.ObjectId,
        ref : 'User',
    },
    followers : [{
        type : mongoose.Schema.Types.ObjectId,
        ref : 'User',
        default : []
    }],
    following: [{
        type : mongoose.Schema.Types.ObjectId,
        ref : 'User',
        default : []   
    }]
},{
    timestamps : true
})

followSchema.methods.toJSON = function() {

    const f = this.toObject()
    delete followObject.__v
    delete followObject.createdAt
    delete followObject.updatedAt
    return followObject
}

const Follow = mongoose.model('Follow', followSchema)
module.exports = Follow