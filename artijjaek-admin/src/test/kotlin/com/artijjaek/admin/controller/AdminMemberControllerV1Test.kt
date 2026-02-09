package com.artijjaek.admin.controller

import com.artijjaek.admin.common.auth.AuthAdminIdArgumentResolver
import com.artijjaek.admin.config.security.WebConfig
import com.artijjaek.admin.dto.response.MemberListPageResponse
import com.artijjaek.admin.dto.response.MemberSimpleResponse
import com.artijjaek.admin.dto.response.MemberStatusCountResponse
import com.artijjaek.admin.enums.MemberListSearchType
import com.artijjaek.admin.enums.MemberListSortBy
import com.artijjaek.admin.enums.MemberStatusFilter
import com.artijjaek.admin.service.AdminMemberService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@ActiveProfiles("test")
@WebMvcTest(AdminMemberControllerV1::class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebConfig::class, AuthAdminIdArgumentResolver::class)
class AdminMemberControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var adminMemberService: AdminMemberService

    @Test
    @WithMockUser(username = "1")
    @DisplayName("회원 목록을 조회한다")
    fun getMemberListTest() {
        // given
        val pageable = PageRequest.of(0, 5)
        val response = MemberListPageResponse(
            pageNumber = 0,
            totalCount = 15,
            hasNext = true,
            statusCount = MemberStatusCountResponse(
                allCount = 15,
                activeCount = 11,
                deletedCount = 4
            ),
            content = listOf(
                MemberSimpleResponse(
                    memberId = 1L,
                    email = "john.doe@example.com",
                    nickname = "John Doe",
                    memberStatus = com.artijjaek.core.domain.member.enums.MemberStatus.ACTIVE,
                    createdAt = LocalDateTime.of(2024, 12, 15, 0, 0)
                )
            )
        )
        every {
            adminMemberService.searchMembers(
                pageable = pageable,
                statusFilter = MemberStatusFilter.ALL,
                searchType = MemberListSearchType.NICKNAME,
                keyword = "john",
                sortBy = MemberListSortBy.SUBSCRIBE_DATE,
                sortDirection = Sort.Direction.DESC
            )
        } returns response

        // when
        val mvcResult = mockMvc.perform(
            get("/admin/v1/member/list")
                .param("page", "0")
                .param("size", "5")
                .param("status", "ALL")
                .param("searchType", "NICKNAME")
                .param("keyword", "john")
                .param("sortBy", "SUBSCRIBE_DATE")
                .param("sortDirection", "DESC")
        )

        // then
        mvcResult
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.data.totalCount").value(15))
            .andExpect(jsonPath("$.data.statusCount.activeCount").value(11))
            .andExpect(jsonPath("$.data.content[0].nickname").value("John Doe"))

        verify(exactly = 1) {
            adminMemberService.searchMembers(
                pageable = pageable,
                statusFilter = MemberStatusFilter.ALL,
                searchType = MemberListSearchType.NICKNAME,
                keyword = "john",
                sortBy = MemberListSortBy.SUBSCRIBE_DATE,
                sortDirection = Sort.Direction.DESC
            )
        }
    }
}
