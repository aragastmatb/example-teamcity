const ReplyService = require('../src/reply-service');

describe('ReplyService', () => {
    let replyService;

    beforeEach(() => {
        replyService = new ReplyService();
    });

    test('should add a reply', () => {
        const reply = {
            content: 'Test reply',
            author: 'testuser'
        };

        const result = replyService.addReply('comment123', reply);
        
        expect(result.id).toBeDefined();
        expect(result.content).toBe('Test reply');
        expect(result.author).toBe('testuser');
        expect(result.commentId).toBe('comment123');
    });

    test('should get reply by id', () => {
        const reply = {
            content: 'Test reply',
            author: 'testuser'
        };

        const addedReply = replyService.addReply('comment123', reply);
        const foundReply = replyService.getReply(addedReply.id);
        
        expect(foundReply).toEqual(addedReply);
    });

    test('should get replies by comment id', () => {
        const reply1 = { content: 'Reply 1', author: 'user1' };
        const reply2 = { content: 'Reply 2', author: 'user2' };

        replyService.addReply('comment123', reply1);
        replyService.addReply('comment123', reply2);
        replyService.addReply('comment456', { content: 'Other', author: 'user3' });

        const replies = replyService.getRepliesByComment('comment123');
        
        expect(replies).toHaveLength(2);
        expect(replies[0].commentId).toBe('comment123');
        expect(replies[1].commentId).toBe('comment123');
    });
});
