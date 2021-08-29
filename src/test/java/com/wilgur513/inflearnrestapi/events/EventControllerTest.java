package com.wilgur513.inflearnrestapi.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wilgur513.inflearnrestapi.accounts.Account;
import com.wilgur513.inflearnrestapi.accounts.AccountRepository;
import com.wilgur513.inflearnrestapi.accounts.AccountRole;
import com.wilgur513.inflearnrestapi.accounts.AccountService;
import com.wilgur513.inflearnrestapi.common.RestDocsConfiguration;
import com.wilgur513.inflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Transactional
public class EventControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @TestDescription("정상적인 이벤트 생성 테스트")
    @Transactional
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .build();

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name"),
                                fieldWithPath("description").description("description"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime"),
                                fieldWithPath("location").description("location"),
                                fieldWithPath("basePrice").description("basePrice"),
                                fieldWithPath("maxPrice").description("maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("name").description("name"),
                                fieldWithPath("description").description("description"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime"),
                                fieldWithPath("location").description("location"),
                                fieldWithPath("basePrice").description("basePrice"),
                                fieldWithPath("maxPrice").description("maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment"),
                                fieldWithPath("offline").description("offline"),
                                fieldWithPath("free").description("free"),
                                fieldWithPath("eventStatus").description("eventStatus"),
                                fieldWithPath("manager").description("manager"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-event.href").description("link to update"),
                                fieldWithPath("_links.query-events.href").description("link to query"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("처리하지 못하는 입력 값에 따른 Bad Request 반환")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    @Test
    @TestDescription("빈 입력 값에 대한 Bad Request 반환")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("잘못된 입력 값에 대한 Bad Request 반환")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .build();

        mockMvc.perform(post("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개 이벤트를 10개씩 두번째 페이지 조회하기")
    @Transactional
    public void queryEvents() throws Exception{
        IntStream.range(0, 30).forEach(this::generateEvent);

        mockMvc.perform(get("/api/events")
                    .param("page", "1")
                    .param("size", "10")
                    .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query-events",
                        links(
                                linkWithRel("first").description("link to first"),
                                linkWithRel("self").description("link to self"),
                                linkWithRel("prev").description("link to previous page"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestParameters(
                                parameterWithName("page").description("page"),
                                parameterWithName("size").description("size"),
                                parameterWithName("sort").description("sort")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventResourceList").type(JsonFieldType.ARRAY).description("result array"),
                                fieldWithPath("_embedded.eventResourceList[].id").description("id"),
                                fieldWithPath("_embedded.eventResourceList[].name").description("name"),
                                fieldWithPath("_embedded.eventResourceList[].description").description("description"),
                                fieldWithPath("_embedded.eventResourceList[].beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].beginEventDateTime").description("beginEventDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].endEventDateTime").description("endEventDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].location").description("location"),
                                fieldWithPath("_embedded.eventResourceList[].basePrice").description("basePrice"),
                                fieldWithPath("_embedded.eventResourceList[].maxPrice").description("maxPrice"),
                                fieldWithPath("_embedded.eventResourceList[].limitOfEnrollment").description("limitOfEnrollment"),
                                fieldWithPath("_embedded.eventResourceList[].offline").description("offline"),
                                fieldWithPath("_embedded.eventResourceList[].free").description("free"),
                                fieldWithPath("_embedded.eventResourceList[].eventStatus").description("eventStatus"),
                                fieldWithPath("_embedded.eventResourceList[].manager").description("manager"),
                                fieldWithPath("_embedded.eventResourceList[]._links.self.href").description("link to self"),
                                fieldWithPath("_links.first.href").description("links"),
                                fieldWithPath("_links.prev.href").description("links"),
                                fieldWithPath("_links.self.href").description("links"),
                                fieldWithPath("_links.next.href").description("links"),
                                fieldWithPath("_links.last.href").description("links"),
                                fieldWithPath("_links.profile.href").description("links"),
                                fieldWithPath("page.size").description("page"),
                                fieldWithPath("page.totalElements").description("page"),
                                fieldWithPath("page.totalPages").description("page"),
                                fieldWithPath("page.number").description("page")
                        )
                ))

        ;
    }

    @Test
    @TestDescription("30개 이벤트를 유저 정보와 함께 10개씩 두번째 페이지 조회하기")
    @Transactional
    public void queryEventsWithAuthentication() throws Exception{
        IntStream.range(0, 30).forEach(this::generateEvent);

        mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("query-events",
                        links(
                                linkWithRel("first").description("link to first"),
                                linkWithRel("self").description("link to self"),
                                linkWithRel("prev").description("link to previous page"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("create-event").description("link to create event")
                        ),
                        requestParameters(
                                parameterWithName("page").description("page"),
                                parameterWithName("size").description("size"),
                                parameterWithName("sort").description("sort")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventResourceList").type(JsonFieldType.ARRAY).description("result array"),
                                fieldWithPath("_embedded.eventResourceList[].id").description("id"),
                                fieldWithPath("_embedded.eventResourceList[].name").description("name"),
                                fieldWithPath("_embedded.eventResourceList[].description").description("description"),
                                fieldWithPath("_embedded.eventResourceList[].beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].beginEventDateTime").description("beginEventDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].endEventDateTime").description("endEventDateTime"),
                                fieldWithPath("_embedded.eventResourceList[].location").description("location"),
                                fieldWithPath("_embedded.eventResourceList[].basePrice").description("basePrice"),
                                fieldWithPath("_embedded.eventResourceList[].maxPrice").description("maxPrice"),
                                fieldWithPath("_embedded.eventResourceList[].limitOfEnrollment").description("limitOfEnrollment"),
                                fieldWithPath("_embedded.eventResourceList[].offline").description("offline"),
                                fieldWithPath("_embedded.eventResourceList[].free").description("free"),
                                fieldWithPath("_embedded.eventResourceList[].eventStatus").description("eventStatus"),
                                fieldWithPath("_embedded.eventResourceList[].manager").description("manager"),
                                fieldWithPath("_embedded.eventResourceList[]._links.self.href").description("link to self"),
                                fieldWithPath("_links.first.href").description("links"),
                                fieldWithPath("_links.prev.href").description("links"),
                                fieldWithPath("_links.self.href").description("links"),
                                fieldWithPath("_links.next.href").description("links"),
                                fieldWithPath("_links.last.href").description("links"),
                                fieldWithPath("_links.profile.href").description("links"),
                                fieldWithPath("_links.create-event.href").description("links"),
                                fieldWithPath("page.size").description("page"),
                                fieldWithPath("page.totalElements").description("page"),
                                fieldWithPath("page.totalPages").description("page"),
                                fieldWithPath("page.number").description("page")
                        )
                ))

        ;
    }

    @Test
    @TestDescription("기존의 이벤트 하나 조회하기")
    @Transactional
    public void queryEvent() throws Exception {
        Event event = generateEvent(0);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("query-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        pathParameters(
                                parameterWithName("id").description("event id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("name").description("name"),
                                fieldWithPath("description").description("description"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime"),
                                fieldWithPath("location").description("location"),
                                fieldWithPath("basePrice").description("basePrice"),
                                fieldWithPath("maxPrice").description("maxPrice"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment"),
                                fieldWithPath("offline").description("offline"),
                                fieldWithPath("free").description("free"),
                                fieldWithPath("eventStatus").description("eventStatus"),
                                fieldWithPath("manager").description("manager"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                    )
                )
        ;
    }

    @Test
    @TestDescription("기존에 없는 이벤트 조회로 인한 Not Found")
    public void queryEvent_Not_Found() throws Exception {
        mockMvc.perform(get("/api/events/100"))
                .andExpect(status().isNotFound());

    }

    @Test
    @TestDescription("기존의 이벤트 수정하기")
    public void updateEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .build();
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        eventRepository.save(event);
        eventDto.setName("Updated");

        mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value("Updated"))
                    .andDo(document("update-event",
                            links(
                                    linkWithRel("self").description("link to self"),
                                    linkWithRel("profile").description("link to profile")
                            ),
                            requestHeaders(
                                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                            ),
                            requestFields(
                                    fieldWithPath("name").description("name"),
                                    fieldWithPath("description").description("description"),
                                    fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                    fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                    fieldWithPath("beginEventDateTime").description("beginEventDateTime"),
                                    fieldWithPath("endEventDateTime").description("endEventDateTime"),
                                    fieldWithPath("location").description("location"),
                                    fieldWithPath("basePrice").description("basePrice"),
                                    fieldWithPath("maxPrice").description("maxPrice"),
                                    fieldWithPath("limitOfEnrollment").description("limitOfEnrollment")
                            ),
                            responseFields(
                                    fieldWithPath("id").description("id"),
                                    fieldWithPath("name").description("name"),
                                    fieldWithPath("description").description("description"),
                                    fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime"),
                                    fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime"),
                                    fieldWithPath("beginEventDateTime").description("beginEventDateTime"),
                                    fieldWithPath("endEventDateTime").description("endEventDateTime"),
                                    fieldWithPath("location").description("location"),
                                    fieldWithPath("basePrice").description("basePrice"),
                                    fieldWithPath("maxPrice").description("maxPrice"),
                                    fieldWithPath("limitOfEnrollment").description("limitOfEnrollment"),
                                    fieldWithPath("offline").description("offline"),
                                    fieldWithPath("free").description("free"),
                                    fieldWithPath("eventStatus").description("eventStatus"),
                                    fieldWithPath("manager").description("manager"),
                                    fieldWithPath("_links.self.href").description("link to self"),
                                    fieldWithPath("_links.profile.href").description("link to profile")
                            )
                    ))
        ;
    }

    @Test
    @TestDescription("비어있는 값으로 이벤트를 변경 시 Bad Request")
    public void updateEvent_Bad_Request_Empty() throws Exception {
        Event event = generateEvent(100);
        EventDto eventDto = new EventDto();
        mockMvc.perform(put("/api/events/{id}", event.getId())
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("잘못된 값으로 이벤트를 변경 시 Bad Request")
    public void updateEvent_Bad_Request_Wrong() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .build();
        Event event = generateEvent(100);
        eventRepository.save(event);
        mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("없는 이벤트 변경 시 Not Found")
    public void updateEvent_Not_Found() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2018, 11, 23, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018, 11, 24, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2018, 11, 25, 14, 21))
                .endEventDateTime(LocalDateTime.of(2018, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2")
                .build();

        mockMvc.perform(put("/api/events/{id}", 100)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andExpect(status().isNotFound())
        ;
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event " + index)
                .description("description " + index)
                .build();
        eventRepository.save(event);
        return event;
    }

    private String getAccessToken() throws Exception {
        accountRepository.deleteAll();
        String username = "wilgur513@email.com";
        String password = "wilgur513";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        ResultActions resultActions = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }
}
