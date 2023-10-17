package com.laioffer.twitch.hello;

import com.github.javafaker.Faker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class HelloController {

    @GetMapping("/hello1")
    public String sayHello() {
        return "Hello World!";
    }

    @GetMapping("/hello2")
    public String sayHello2() {
        return "Hello World 2!";
    }
    @GetMapping("/hello3")
    public String sayHello3() {
        return "Hello World 3!";
    }

    @GetMapping("/hello")
    public String sayHello4() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String company = faker.company().name();
        String street = faker.address().streetAddress();
        String city = faker.address().city();
        String state = faker.address().state();
        String bookTitle = faker.book().title();
        String bookAuthor = faker.book().author();
        // Formatted String:
        String template = "This is %s. I work at %s. I live at %s in %s %s. My favorite book is %s by %s.";
        // 替换字符由.formatted来完成；

        return template.formatted(
                name,
                company,
                street,
                city,
                state,
                bookTitle,
                bookAuthor
        );
    }

    @GetMapping("/hello5")
    public Person sayHello5() {
        Faker faker2 = new Faker();
        String name = faker2.name().fullName();
        String company = faker2.company().name();
        String street = faker2.address().streetAddress();
        String city = faker2.address().city();
        String state = faker2.address().state();
        String bookTitle = faker2.book().title();
        String bookAuthor = faker2.book().author();


        return new Person(
                name,
                company,
                new Address(street, city, state, null),
                new Book(bookTitle, bookAuthor)
        );


    }

    @GetMapping("/hello6")
    public Person sayHello6(@RequestParam(required = false) String locale) {
        if (locale == null) {
            locale = "en_US";
        }
        Faker faker = new Faker(new Locale(locale));
        String name = faker.name().fullName();
        String company = faker.company().name();
        String street = faker.address().streetAddress();
        String city = faker.address().city();
        String state = faker.address().state();
        String bookTitle = faker.book().title();
        String bookAuthor = faker.book().author();
        String country = faker.address().country();
        return new Person(
                name,
                company,
                new Address(street, city, state, country),
                new Book(bookTitle, bookAuthor)
        );
        //请求中文网址：http://localhost:8080/hello6?locale=zh-CN
    }








}
