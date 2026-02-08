// Reply Service v1.0.0
class ReplyService {
constructor() {
this.replies = new Map();
}

text
addReply(commentId, reply) {
    const replyId = Date.now().toString();
    const replyData = {
        id: replyId,
        commentId,
        content: reply.content,
        author: reply.author,
        createdAt: new Date()
    };
    
    this.replies.set(replyId, replyData);
    return replyData;
}

getReply(id) {
    return this.replies.get(id);
}

getRepliesByComment(commentId) {
    return Array.from(this.replies.values())
        .filter(reply => reply.commentId === commentId);
}
}

module.exports = ReplyService;
