interface Handler {
    setNext(handler: Handler): Handler;
    handle(request: Request): Response | null;
}

abstract class AbstractHandler implements Handler {
    private nextHandler: Handler | null = null;
    
    setNext(handler: Handler): Handler {
        this.nextHandler = handler;
        return handler;
    }
    
    handle(request: Request): Response | null {
        if (this.nextHandler) {
            return this.nextHandler.handle(request);
        }
        return null;
    }
}

class AuthHandler extends AbstractHandler {
    handle(request: Request): Response | null {
        if (!request.token) {
            return {error: "Unauthorized"};
        }
        return super.handle(request);
    }
}

// Chain setup
auth.setNext(rateLimit).setNext(validation).setNext(processor);
