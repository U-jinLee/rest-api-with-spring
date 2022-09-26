package com.yoojin.restapiwithspring.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoojin.restapiwithspring.common.RestDocsConfiguration;
import com.yoojin.restapiwithspring.common.TestDescription;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(RestDocsConfiguration.class)
@AutoConfigureRestDocs
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Ignore
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트 생성하는 테스트")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 06, 03, 10, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 07, 03, 10, 30))
                .beginEventDateTime(LocalDateTime.of(2020, 06, 03, 10, 30))
                .endEventDateTime(LocalDateTime.of(2020, 07, 03, 10, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();

        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update").exists())
                .andExpect(jsonPath("_links.profile").exists())
                //문서의 제목 추가
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query event"),
                                linkWithRel("update").description("link to update"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("Description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("BeginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("CloseEnrollmentDateTime of new Event"),
                                fieldWithPath("beginEventDateTime").description("BeginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("EndEventDateTime of new Event"),
                                fieldWithPath("location").description("Location of new Event"),
                                fieldWithPath("basePrice").description("BasePrice of new Event"),
                                fieldWithPath("maxPrice").description("MaxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("LimitOfEnrollment of new Event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of new Event"),
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("Description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("BeginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("CloseEnrollmentDateTime of new Event"),
                                fieldWithPath("beginEventDateTime").description("BeginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("EndEventDateTime of new Event"),
                                fieldWithPath("location").description("Location of new Event"),
                                fieldWithPath("basePrice").description("BasePrice of new Event"),
                                fieldWithPath("maxPrice").description("MaxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("LimitOfEnrollment of new Event"),
                                fieldWithPath("free").description("free of new Event"),
                                fieldWithPath("offline").description("offline of new Event"),
                                fieldWithPath("eventStatus").description("eventStatus of new Event"),
                                /**Response field 있지만 무시하고 싶은 값**/
                                fieldWithPath("_links.*").ignored(),
                                fieldWithPath("_links.self.*").ignored(),
                                fieldWithPath("_links.query-events.*").ignored(),
                                fieldWithPath("_links.update.*").ignored(),
                                fieldWithPath("_links.profile.href").ignored()
                        )
                        ));

    }

    @Test
    public void createEvent_bad_request() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 06, 03, 10, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 07, 03, 10, 30))
                .beginEventDateTime(LocalDateTime.of(2020, 06, 03, 10, 30))
                .endEventDateTime(LocalDateTime.of(2020, 07, 03, 10, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();


        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void createEvent_bad_request_empty_input() throws Exception {

        EventDto eventDto = EventDto.builder().build();


        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createEvent_bad_request_wrong_input() throws Exception {

        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 06, 03, 10, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 07, 03, 10, 30))
                .beginEventDateTime(LocalDateTime.of(2020, 06, 03, 10, 30))
                .endEventDateTime(LocalDateTime.of(2020, 07, 02, 10, 30))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역")
                .build();


        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());

    }

    @Test
    @TestDescription("30개의 이벤트를 10개 씩 두번 째 페이지 조회")
    public void queryEvents() throws Exception {
        //given
        IntStream.range(0, 30).forEach(this::generateEvent);
        //when
        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
                )
                //then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query_events"));

    }

    private void generateEvent(int index) {
        Event event = Event.builder()
                .name("event "+index)
                .description("test event")
                .build();
        this.eventRepository.save(event);
    }
}