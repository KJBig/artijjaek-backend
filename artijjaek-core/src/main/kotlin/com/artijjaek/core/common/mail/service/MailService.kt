package com.artijjaek.core.common.mail.service

import com.artijjaek.core.common.mail.dto.ArticleMailDto
import com.artijjaek.core.common.mail.dto.MemberMailDto
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate

@Service
class MailService(
    private val javaMailSender: JavaMailSender,
) {

    private val log = LoggerFactory.getLogger(MailService::class.java)

    @Async("asyncEmailThreadPoolExecutor")
    fun sendArticleMail(memberData: MemberMailDto, articleDatas: List<ArticleMailDto>) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val today = LocalDate.now()

        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")

            // 수신자/제목
            mimeMessageHelper.setTo(memberData.email)
            mimeMessageHelper.setFrom("noreply@artijjaek.kr", "아티짹")
            mimeMessageHelper.setReplyTo("noreply@artijjaek.kr")
            mimeMessageHelper.setSubject("[아티짹] ${today} 아티클 목록")

            val dayOfWeekShort = getDayOfWeekShort(LocalDate.now())

            // HTML 본문
            val content = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>[${today}] 아티클 목록</title>
                </head>
                
                <body style="margin:0;padding:0;background-color:#ffffff;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
                  <!-- Outer -->
                  <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="background-color:#ffffff;padding:20px 0;">
                    <tr>
                      <td align="center" style="padding:0 12px;">
                        
                        <!-- Container -->
                        <table role="presentation" width="800" cellspacing="0" cellpadding="0" border="0" style="width:100%;max-width:800px;border-collapse:separate;border-spacing:0;">
                          <tr>
                            <td style="border:1px solid #e1e5e9;border-radius:12px;overflow:hidden;background-color:#ffffff;">
                
                              <!-- Header -->
                              <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                <tr>
                                  <td align="center" style="padding:24px;background:#667eea;background-image:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#ffffff;">
                                    <div style="margin:0;font-size:24px;font-weight:700;line-height:1.3;">오늘의 아티클</div>
                                    <div style="margin-top:8px;opacity:0.9;font-size:14px;line-height:1.4;">
                                      ${today} (${dayOfWeekShort}) | 총 ${articleDatas.size}개의 아티클
                                    </div>
                                  </td>
                                </tr>
                              </table>
                
                              <!-- Content (gray bg) -->
                              <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;background-color:#f7f6f3;">
                                <tr>
                                  <td style="padding:24px;">
                                    <!-- Greeting Card -->
                                    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:separate;border-spacing:0;background-color:#ffffff;border:1px solid #e1e5e9;border-radius:8px;">
                                      <tr>
                                        <td style="padding:0;">
                                          <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                            <tr>
                                              <td style="width:4px;background-color:#667eea;border-top-left-radius:8px;border-bottom-left-radius:8px;font-size:0;line-height:0;">&nbsp;</td>
                                              <td style="padding:20px;">
                                              <!-- ✅ Mascot -->
                                                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                                  <tr>
                                                    <td align="center" style="padding:0 0 20px;">
                                                      <img
                                                        src="https://www.artijjaek.kr/main_logo.png"
                                                        alt="티짹"
                                                        width="120"
                                                        style="display:block;border:0;outline:none;text-decoration:none;width:120px;max-width:60%;height:auto;margin:0 auto;"
                                                      />
                                                    </td>
                                                  </tr>
                                                </table>
                                                <div style="margin:0 0 8px 0;color:rgb(55,53,47);font-size:18px;font-weight:800;line-height:1.3;">
                                                  안녕하세요, ${memberData.nickname}님!
                                                </div>
                                                <div style="margin:0;color:rgb(120,119,116);font-size:14px;line-height:1.5;">
                                                  어제 하루 동안 게시된 아티클입니다. 각 아티클을 클릭하면 원문으로 이동합니다.
                                                </div>
                                              </td>
                                            </tr>
                                          </table>
                                        </td>
                                      </tr>
                                    </table>
                
                                    <!-- Spacer -->
                                    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                      <tr><td style="height:16px;line-height:16px;font-size:0;">&nbsp;</td></tr>
                                    </table>
                
                                    <!-- Articles Header -->
                                    <div style="margin:0 0 16px 0;color:rgb(55,53,47);font-size:16px;font-weight:800;line-height:1.3;">
                                      아티클 목록
                                    </div>
                
                                    <!-- Cards -->
                                    ${generateBookmarkCards(articleDatas)}
                
                                  </td>
                                </tr>
                              </table>
                
                              <!-- Footer -->
                              <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;background-color:#ffffff;border-top:1px solid #e1e5e9;">
                                <tr>
                                  <td align="center" style="padding:24px;">
                                    <!-- Button (table-based) -->
                                    <table role="presentation" cellspacing="0" cellpadding="0" border="0" style="margin:0 auto 16px auto;">
                                      <tr>
                                        <td style="border-radius:6px;background:#667eea;background-image:linear-gradient(135deg,#667eea 0%,#764ba2 100%);">
                                          <a href="https://www.artijjaek.kr/setting?email=${memberData.email}&token=${memberData.uuidToken}"
                                             style="display:inline-block;padding:12px 24px;color:#ffffff !important;text-decoration:none;border-radius:6px;font-weight:600;font-size:14px;">
                                            ⚙️ 구독 설정
                                          </a>
                                        </td>
                                      </tr>
                                    </table>
                
                                    <div style="margin:0;color:rgb(120,119,116);font-size:12px;line-height:1.4;">
                                      이 메일은 자동으로 발송되었습니다.<br />
                                    </div>
                                  </td>
                                </tr>
                              </table>
                
                            </td>
                          </tr>
                        </table>
                
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
            """.trimIndent()

            mimeMessageHelper.setText(content, true)
            javaMailSender.send(mimeMessage)
            log.info("신규 아티클 메일 발송 성공!")
        } catch (e: Exception) {
            log.error("신규 아티클 메일 발송 실패!", e)
            throw RuntimeException(e)
        }
    }

    private fun generateBookmarkCards(articleDatas: List<ArticleMailDto>): String {
        return articleDatas.joinToString("\n") { articleData ->
            val safeLink = articleData.link.takeIf { it.isNotBlank() } ?: "#"
            val safeTitle = cleanText(articleData.title)

            val logoHtml = if (articleData.companyLogo.isNotBlank()) {
                """
                    <img src="${articleData.companyLogo}" alt="favicon" width="16" height="16"
                     style="display:inline-block;border:0;outline:none;text-decoration:none;width:16px;height:16px;border-radius:2px;vertical-align:middle;margin-right:6px;" />
                """.trimIndent()
            } else {
                """
                    <span style="display:inline-block;width:16px;height:16px;border-radius:2px;background:#e1e5e9;vertical-align:middle;margin-right:6px;"></span>
                """.trimIndent()
            }

            val imageTd = if (!articleData.image.isNullOrBlank()) {
                """
                    <td width="180" valign="top" style="padding:0;">
                      <img src="${articleData.image}" alt="썸네일" width="180" height="120"
                           style="display:block;border:0;outline:none;text-decoration:none;width:180px;height:120px;background:#f1f1ef;object-fit:cover;" />
                    </td>
                """.trimIndent()
            } else {
                "" // 이미지 없으면 우측 td 자체를 만들지 않음
            }

            """
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0"
                       style="border-collapse:separate;border-spacing:0;background-color:#ffffff;border:1px solid #e1e5e9;border-radius:8px;overflow:hidden;margin-bottom:16px;">
                  <tr>
                    <td style="padding:0;">
        
                      <!-- ✅ 카드 전체 링크 -->
                      <a href="$safeLink"
                         style="display:block;width:100%;height:100%;text-decoration:none;color:inherit;">
        
                        <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0"
                               style="border-collapse:collapse;">
                          <tr>
                            <!-- 텍스트 영역 -->
                            <td valign="top" style="padding:16px;">
                              <div style="margin:0 0 8px 0;font-size:16px;font-weight:800;line-height:1.3;color:rgb(55,53,47);">
                                $safeTitle
                              </div>
        
                              <div style="margin:0;font-size:12px;line-height:1.4;color:rgb(120,119,116);">
                                $logoHtml
                                <span style="vertical-align:middle;">${articleData.companyNameKr}</span>
                              </div>
                            </td>
        
                            <!-- 이미지 영역 -->
                            $imageTd
                          </tr>
                        </table>
                      </a>
        
                    </td>
                  </tr>
                </table>
            """.trimIndent()
        }
    }

    // HTML 텍스트를 안전하게 처리
    private fun cleanText(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .trim()
            .take(200)
    }

    private fun getDayOfWeekShort(date: LocalDate): String {
        return when (date.dayOfWeek) {
            DayOfWeek.MONDAY -> "월"
            DayOfWeek.TUESDAY -> "화"
            DayOfWeek.WEDNESDAY -> "수"
            DayOfWeek.THURSDAY -> "목"
            DayOfWeek.FRIDAY -> "금"
            DayOfWeek.SATURDAY -> "토"
            DayOfWeek.SUNDAY -> "일"
            else -> ""
        }
    }

    @Async("asyncEmailThreadPoolExecutor")
    fun sendSubscribeMail(memberData: MemberMailDto) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val today = LocalDate.now()

        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")

            // 수신자/제목
            mimeMessageHelper.setTo(memberData.email)
            mimeMessageHelper.setFrom("noreply@artijjaek.kr", "아티짹")
            mimeMessageHelper.setReplyTo("noreply@artijjaek.kr")
            mimeMessageHelper.setSubject("[아티짹] 환영합니다 ${memberData.nickname}님!")

            val content = """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <title>[아티짹] 환영합니다!</title>
            </head>
            
            <body style="margin:0;padding:0;background-color:#ffffff;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif;">
              <!-- ✅ Outer Wrapper -->
              <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="background-color:#ffffff;padding:20px 0;">
                <tr>
                  <td align="center" style="padding:0 12px;">
                    <!-- ✅ Container (max-width 800) -->
                    <table role="presentation" width="800" cellspacing="0" cellpadding="0" border="0" style="width:100%;max-width:800px;border-collapse:separate;border-spacing:0;">
                      <tr>
                        <td style="border:1px solid #e1e5e9;border-radius:16px;overflow:hidden;">
                          <!-- ✅ Header -->
                          <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                            <tr>
                              <td align="center" style="padding:32px 24px 56px;background:#667eea;background-image:linear-gradient(135deg,#667eea 0%,#764ba2 100%);color:#ffffff;">
                                <div style="font-size:26px;font-weight:800;letter-spacing:-0.2px;line-height:1.2;margin:0;">
                                  아티짹 구독을 시작했어요 🎉
                                </div>
                                <div style="margin-top:8px;opacity:0.92;font-size:14px;line-height:1.4;">
                                  ${memberData.nickname}님을 환영합니다!
                                </div>
                              </td>
                            </tr>
                          </table>
            
                          <!-- ✅ Content Area (gray bg) -->
                          <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;background-color:#f7f6f3;">
                            <tr>
                              <td style="padding:24px;">
                                <!-- ✅ Mascot -->
                                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                  <tr>
                                    <td align="center" style="padding:0 0 20px;">
                                      <img
                                        src="https://www.artijjaek.kr/welcome.png"
                                        alt="티짹"
                                        width="120"
                                        style="display:block;border:0;outline:none;text-decoration:none;width:120px;max-width:60%;height:auto;margin:0 auto;"
                                      />
                                    </td>
                                  </tr>
                                </table>
            
                                <!-- ✅ Card 1 (Greeting) -->
                                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:separate;border-spacing:0;background-color:#ffffff;border:1px solid #e1e5e9;border-radius:12px;">
                                  <tr>
                                    <td style="padding:0;">
                                      <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                        <tr>
                                          <td style="width:4px;background-color:#667eea;border-top-left-radius:12px;border-bottom-left-radius:12px;font-size:0;line-height:0;">
                                            &nbsp;
                                          </td>
                                          <td style="padding:20px;">
                                            <div style="margin:0 0 8px 0;color:rgb(55,53,47);font-size:18px;font-weight:800;line-height:1.3;">
                                              안녕하세요, ${memberData.nickname}님!
                                            </div>
                                            <div style="margin:0;color:rgb(120,119,116);font-size:14px;line-height:1.6;">
                                              아티짹 구독을 시작해주셔서 감사합니다.<br />
                                              이제부터 <strong>관심 기업</strong>과 <strong>선택한 카테고리</strong> 기준으로,
                                              새로운 아티클을 놓치지 않게 챙겨드릴게요.
                                            </div>
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
            
                                <!-- Spacer -->
                                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                  <tr><td style="height:16px;line-height:16px;font-size:0;">&nbsp;</td></tr>
                                </table>
            
                                <!-- ✅ Card 2 (How it works) -->
                                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:separate;border-spacing:0;background-color:#ffffff;border:1px solid #e1e5e9;border-radius:12px;">
                                  <tr>
                                    <td style="padding:20px;">
                                      <div style="margin:0 0 10px 0;color:rgb(55,53,47);font-size:16px;font-weight:800;line-height:1.3;">
                                        📌 앞으로 이렇게 보내드려요
                                      </div>
            
                                      <ul style="margin:0;padding:0 0 0 18px;color:rgb(120,119,116);font-size:14px;line-height:1.7;">
                                        <li style="margin:6px 0;">${memberData.nickname}님이 설정한 <strong>기업 블로그</strong>에서 <strong>선택한 카테고리</strong> 아티클이 올라오면 수집해요.</li>
                                        <li style="margin:6px 0;"><strong>매일 아침 7시</strong>에 아티클 목록을 모아 <strong>이메일로 한 번에</strong> 보내드려요.</li>
                                        <li style="margin:6px 0;">이메일은 마스코트 <strong>티짹이</strong>가 아침마다 전달할 거예요.</li>
                                      </ul>
                                    </td>
                                  </tr>
                                </table>
            
                                <!-- Spacer -->
                                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:collapse;">
                                  <tr><td style="height:16px;line-height:16px;font-size:0;">&nbsp;</td></tr>
                                </table>
            
                                <!-- ✅ Card 3 (Thanks + Button) -->
                                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse:separate;border-spacing:0;background-color:#ffffff;border:1px solid #e1e5e9;border-radius:12px;">
                                  <tr>
                                    <td style="padding:20px;">
                                      <div style="margin:0 0 10px 0;color:rgb(55,53,47);font-size:16px;font-weight:800;line-height:1.3;">
                                        💙 구독해주셔서 고맙습니다
                                      </div>
            
                                      <div style="margin:0;color:rgb(120,119,116);font-size:14px;line-height:1.7;">
                                        불편한 점이나 개선 아이디어가 떠오르면 언제든지
                                        
                                         <a href="https://www.artijjaek.kr/inquiry">
                                        문의하기
                                        </a>
                                        
                                        로 알려주세요!
                                      </div>

                                    </td>
                                  </tr>
                                </table>
            
                              </td>
                            </tr>
                          </table>
            
                        </td>
                      </tr>
                    </table>
            
                    <!-- mobile breathing room -->
                    <div style="line-height:0;font-size:0;height:0;">&nbsp;</div>
                  </td>
                </tr>
              </table>
            </body>
            </html>

            """.trimIndent()



            mimeMessageHelper.setText(content, true)
            javaMailSender.send(mimeMessage)
            log.info("구독 시작 메일 발송 성공!")
        } catch (e: Exception) {
            log.error("구독 시작 메일 발송 실패!", e)
            throw RuntimeException(e)
        }
    }

}