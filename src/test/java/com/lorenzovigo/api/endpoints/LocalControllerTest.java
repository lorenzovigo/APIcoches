package com.lorenzovigo.api.endpoints;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LocalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getReturnsOkStatus() throws Exception {
        this.mockMvc.perform(get("/concesionarios")).andExpect(status().isOk());
    }

    @Test
    public void getByIdReturnsNotFoundWithEmptyRepository() throws Exception {
        this.mockMvc.perform(get("/concesionarios/15000")).andExpect(status().isNotFound());
    }

    @Test
    public void pushAndGetByIdWork() throws Exception {
        this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 26\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        this.mockMvc.perform(get("/concesionarios/1")).andExpect(status().isOk());
    }

    @Test
    public void pushTwiceCausesError() throws Exception {
        this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());


    }

}