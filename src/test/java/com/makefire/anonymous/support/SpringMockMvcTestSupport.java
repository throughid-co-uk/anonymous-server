package com.makefire.anonymous.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public abstract class SpringMockMvcTestSupport extends SpringTestSupport {

    @Autowired
    protected MockMvc mockMvc;

}