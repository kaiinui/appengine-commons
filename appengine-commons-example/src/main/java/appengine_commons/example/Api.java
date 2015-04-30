package appengine_commons.example;

import com.google.api.server.spi.config.ApiMethod;

/**
 * Created by kaiinui on 2015/04/30.
 */
@com.google.api.server.spi.config.Api
public class Api {
    @ApiMethod
    public Book getBook() {
        final Book book = new Book();
        book.title = "hoge";
        book.authorName = "Tailor Swift";
        return book;
    }
}
