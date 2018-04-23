;  MIT License

;  Copyright guenchi (c) 2018 
         
;  Permission is hereby granted, free of charge, to any person obtaining a copy
;  of this software and associated documentation files (the "Software"), to deal
;  in the Software without restriction, including without limitation the rights
;  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
;  copies of the Software, and to permit persons to whom the Software is
;  furnished to do so, subject to the following conditions:
         
;  The above copyright notice and this permission notice shall be included in all
;  copies or substantial portions of the Software.
         
;  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
;  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
;  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
;  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
;  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
;  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
;  SOFTWARE.





(library (ballista ballista)
  (export
    get
    post
    res
    send
    staticpath
    listen-on
    server-on
    host?
    user-agent?
    accept-language?
    accept-encoding?
    cookie?
    connection?
    query-parser
  )
  (import
    (scheme)
    (igropyr http)
    (igropyr igropyr)
    (catapult catapult)
  )

    (define route-get (list '()))
    (define route-post (list '()))
    (define server-setup (list (cons 'init '()))) 
 


    (define push
	(lambda (lst x y)
		(if (null? (cdr lst))
			(if (null? (car lst))
				(set-car! lst (cons x y))
				(set-cdr! lst (cons (cons x y) '())))
			(push (cdr lst) x y))))


    (define-syntax iterator
        (lambda (x)
            (syntax-case x ()
                ((_ f1 f2) #'(f1 f2))
                ((_ f1 f2 f3 ...) #'(iterator (f1 f2) f3 ...)))))



    (define-syntax get
        (lambda (x)
            (syntax-case x ()
                ((_ p f1) #'(push route-get p f1))
                ((_ p f1 f2 ...) #'(push route-get p 
                                    (lambda (x y z)
                                        (call/cc 
                                            (lambda (break)
                                                (iterator (f1 x y z break) f2 ...)))))))))        


    (define-syntax post
        (lambda (x)
            (syntax-case x ()
                ((_ p f1) #'(push route-post p f1))
                ((_ p f1 f2 ...) #'(push route-post p 
                                    (lambda (x y z)
                                        (call/cc 
                                            (lambda (break)
                                                (iterator (f1 x y z break) f2 ...)))))))))


 
    (define handle-get
        (request
            (lambda (header path query)
                ((router route-get path) header path query))))
 

    (define handle-post
        (request
            (lambda (header path payload)
                ((router route-post path) header path payload))))
 

    (define staticpath
        (lambda (x)
            (push server-setup 'staticpath x)))


    (define-syntax listen-on
        (lambda (x)
            (syntax-case x ()
                ((_ e) #'(cond 
                    ((string? e) (push server-setup 'ip e))
                    ((integer? e) (push server-setup 'port e))))
                ((_ e1 e2) #'(begin
                                (push server-setup 'ip e1)
                                (push server-setup 'port e2))))))


    (define server-on
        (lambda ()
            (server handle-get handle-post server-setup server-setup))) 
 

    (define handle403
        (lambda x
            (errorpage 403 "<center><h5>Powered by Ballista</h5></center>")))

    (define handle404
        (lambda x
            (errorpage 404 "<center><h5>Powered by Ballista</h5></center>")))
 
 

)





