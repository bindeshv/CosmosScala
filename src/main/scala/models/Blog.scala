package models

import scala.beans.BeanProperty
 class Blog  {
    @BeanProperty var id: String = null 
    @BeanProperty var title: String = null
    @BeanProperty var author: Author = null
    @BeanProperty var publishedOn: String = null 
    @BeanProperty var url: String = null 
    @BeanProperty var tags: Array[String] = null 
    @BeanProperty var product: String = null 

}
//companion
object Blog {
    
    def apply(id: String, title: String, author: Author, publishedOn: String, 
        url: String, tags: Array[String],
        product: String
         ): Blog= {

        var blog = new Blog
        blog.id = id 
        blog.title = title
        blog.author = author
        blog.publishedOn = publishedOn
        blog.url = url 
        blog.tags = tags 
        blog.product = product
        blog
    }
}
