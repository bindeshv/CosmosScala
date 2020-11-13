package models
import scala.beans.BeanProperty

class Author{
    @BeanProperty var firstName: String = null
    @BeanProperty var lastName: String = null
}
//companion
object Author {

    def apply(firstName: String, lastName: String): Author= {
        var author = new Author
        author.firstName = firstName
        author.lastName = lastName
        author 
    }
    
}