Best Practice

Path Variable - mainly used to identify and get individual resources/objects
(ex: get a specific book)
(mainly used for DELETE/GET)


Query Param/Path param - mainly used to sort/filter items 
(ex: in book resource I want to get all the books written by X)
(mainly used for GET)


RequestBody - mainly used for saving resource(s)/object(s)
(ex: I want to add a book)
(mainly used for PUT/POST)



IMPORTANT - never use verbs for endpoints, use plural nouns (instead of /getbooks/ -> /books)


