package com.lorenzovigo.api.endpoints;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CocheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getReturnsOkStatus() throws Exception {
        this.mockMvc.perform(get("/coches")).andExpect(status().isOk());
    }

    @Test
    public void checkSortGivesDifferentResults() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + id + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + id + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
        ResultActions firstResults = this.mockMvc.perform(get("/coches?sort=IA")).andExpect(status().isOk());
        ResultActions secondResults = this.mockMvc.perform(get("/coches?sort=ID")).andExpect(status().isOk());
        assert(!firstResults.equals(secondResults));
    }

    @Test
    public void getCarsByLocalOkStatus() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(get("/concesionarios/" + id + "/coches")).andExpect(status().isOk());
    }

    @Test
    public void getCarsByLocalWithIllegalId() throws Exception {
        MvcResult result = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(get("/concesionarios/" + "-10" + "/coches?sort=")).andExpect(status().isNotFound());
    }

    @Test
    public void getCarsByIdOkStatus() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk());
    }

    @Test
    public void getCarsByIdNotFoundWhenwrongId() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        this.mockMvc.perform(get("/coches/" + "1000000")).andExpect(status().isNotFound());
    }

    @Test
    public void deleteForceByIdOkStatus() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();

        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(delete("/coches/" + carId + "/force")).andExpect(status().isNoContent());
        this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isNotFound());
    }

    @Test
    public void sellByIdOkStatus() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/vender?precio=10000")).andExpect(status().isOk());

        MvcResult finalResult = this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk()).andReturn();
        boolean vendido = JsonPath.read(finalResult.getResponse().getContentAsString(), "$.vendido");
        assert(vendido);
    }

    @Test
    public void sellByIdWithoutPriceBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/vender")).andExpect(status().isBadRequest());
    }

    @Test
    public void sellByIdAlreadySoldBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/vender?precio=10000")).andExpect(status().isOk());

        MvcResult finalResult = this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk()).andReturn();
        boolean vendido = JsonPath.read(finalResult.getResponse().getContentAsString(), "$.vendido");
        assert(vendido);
        this.mockMvc.perform(put("/coches/" + carId + "/vender?precio=10000")).andExpect(status().isBadRequest());
    }

    @Test
    public void sellByIdUnavailabledBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(delete("/coches/" + carId)).andExpect(status().isOk());
        this.mockMvc.perform(put("/coches/" + carId + "/vender?precio=10000")).andExpect(status().isBadRequest());
    }

    @Test
    public void matricularByIdOkStatus() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/matricular?matricula=TEST")).andExpect(status().isOk());

        MvcResult finalResult = this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk()).andReturn();
        String matricula = JsonPath.read(finalResult.getResponse().getContentAsString(), "$.matricula");
        assertEquals("TEST", matricula);
    }

    @Test
    public void matriculaByIdWithoutMatriculaBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/matricular")).andExpect(status().isBadRequest());
    }

    @Test
    public void matriculaByIdAlreadySoldBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/vender?precio=10000")).andExpect(status().isOk());

        MvcResult finalResult = this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk()).andReturn();
        boolean vendido = JsonPath.read(finalResult.getResponse().getContentAsString(), "$.vendido");
        assert(vendido);
        this.mockMvc.perform(put("/coches/" + carId + "/matricular?matricula=TEST")).andExpect(status().isBadRequest());
    }

    @Test
    public void matriculaByIdUnavailabledBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(delete("/coches/" + carId)).andExpect(status().isOk());
        this.mockMvc.perform(put("/coches/" + carId + "/matricular?matricula=10000")).andExpect(status().isBadRequest());
    }

    @Test
    public void matricularTwiceByIdOkStatus() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/matricular?matricula=TEST")).andExpect(status().isOk());

        MvcResult finalResult = this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk()).andReturn();
        String matricula = JsonPath.read(finalResult.getResponse().getContentAsString(), "$.matricula");
        assertEquals("TEST", matricula);

        this.mockMvc.perform(put("/coches/" + carId + "/matricular?matricula=TEST2")).andExpect(status().isOk());
        finalResult = this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk()).andReturn();
        matricula = JsonPath.read(finalResult.getResponse().getContentAsString(), "$.matricula");
        assertEquals("TEST2", matricula);
    }

    @Test
    public void unavailableByIdOkStatus() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(delete("/coches/" + carId)).andExpect(status().isOk());
    }

    @Test
    public void unavailableaByIdAlreadySoldBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(put("/coches/" + carId + "/vender?precio=10000")).andExpect(status().isOk());

        MvcResult finalResult = this.mockMvc.perform(get("/coches/" + carId)).andExpect(status().isOk()).andReturn();
        boolean vendido = JsonPath.read(finalResult.getResponse().getContentAsString(), "$.vendido");
        assert(vendido);
        this.mockMvc.perform(delete("/coches/" + carId)).andExpect(status().isBadRequest());
    }

    @Test
    public void unavailableByIdAlreadyUnavailabledBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        MvcResult carResult = this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + "}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int carId = JsonPath.read(carResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(delete("/coches/" + carId)).andExpect(status().isOk());
        this.mockMvc.perform(delete("/coches/" + carId)).andExpect(status().isBadRequest());
    }

    @Test
    public void pushWithUnexistingLocalIdNotFound() throws Exception {
        this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": 300}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void pushWithBadFormatDateBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + ", \"fechaIngreso\": \"AAA\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void pushSoldWithNoFechaVentaBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + ", \"vendido\": \"true\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void pushFechaVentaButNotSoldBadRequest() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + ", \"fechaVenta\": \"2020-12-03 12:20:10\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void pushOk() throws Exception {
        MvcResult localResult = this.mockMvc.perform(post("/concesionarios").content("{\"direccion\": \"Carrer d'Alcala 27\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        int localId = JsonPath.read(localResult.getResponse().getContentAsString(), "$.id");

        this.mockMvc.perform(post("/coches").content("{\"marca\": \"Seat\", \"localId\": " + localId + ", \"vendido\": \"true\", \"fechaVenta\": \"2020-12-03 12:20:10\", \"matricula\": \"AAAAA\", \"precio\": 20000, \"coste\": 8000}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }

}