package com.artijjaek.core.common.mail.service

import com.artijjaek.core.domain.article.entity.Article
import com.artijjaek.core.domain.member.entity.Member
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate

@Service
class MailService(
    private val javaMailSender: JavaMailSender,
) {

    private val log = LoggerFactory.getLogger(MailService::class.java)

    fun sendArticleMail(member: Member, articles: List<Article>) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val today = LocalDate.now()

        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")

            // 수신자/제목
            mimeMessageHelper.setTo(member.email)
            mimeMessageHelper.setFrom("noreply@artijjaek.kr", "아티짹")
            mimeMessageHelper.setReplyTo("noreply@artijjaek.kr")
            mimeMessageHelper.setSubject("[아티짹] ${today} 아티클 목록")

            val dayOfWeekShort = getDayOfWeekShort(LocalDate.now())

            // HTML 본문
            val content = """
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>[${today}] 아티클 목록</title>
                    <style>
                        body {
                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
                            background-color: #f7f6f3;
                            margin: 0;
                            padding: 20px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 0 auto;
                            background: white;
                            border-radius: 12px;
                            overflow: hidden;
                            box-shadow: rgba(0, 0, 0, 0.1) 0px 4px 12px;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 24px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 24px;
                            font-weight: 700;
                        }
                        .header p {
                            margin: 8px 0 0 0;
                            opacity: 0.9;
                            font-size: 14px;
                        }
                        .content {
                            padding: 24px;
                            background: #f7f6f3;
                        }
                        .greeting {
                            margin-bottom: 24px;
                            padding: 20px;
                            background: white;
                            border-radius: 8px;
                            border-left: 4px solid #667eea;
                        }
                        .greeting h2 {
                            margin: 0 0 8px 0;
                            color: rgb(55, 53, 47);
                            font-size: 18px;
                        }
                        .greeting p {
                            margin: 0;
                            color: rgb(120, 119, 116);
                            line-height: 1.5;
                        }
                        .articles-header {
                            margin-bottom: 16px;
                            color: rgb(55, 53, 47);
                            font-size: 16px;
                            font-weight: 800;
                        }

                        /* 카드 */
                        .bookmark-card {
                            display: block;
                            text-decoration: none;
                            color: inherit;
                            background: white;
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: rgba(15, 15, 15, 0.1) 0px 0px 0px 1px, rgba(15, 15, 15, 0.1) 0px 2px 4px;
                            margin-bottom: 16px;
                            transition: all 0.2s ease;
                        }
                        .bookmark-content {
                            display: flex;
                            align-items: stretch;
                            min-height: 120px;
                        }
                        .bookmark-text {
                            flex: 1 1 auto;
                            min-width: 0; /* ellipsis 필수 */
                            padding: 16px;
                            display: flex;
                            flex-direction: column;
                            justify-content: space-between;
                        }
                        .bookmark-title {
                            font-size: 16px;
                            font-weight: 800;
                            color: rgb(55, 53, 47);
                            line-height: 1.3;
                            margin-bottom: 8px;
                        }
                        .bookmark-description {
                            font-size: 14px;
                            color: rgb(120, 119, 116);
                            line-height: 1.4;
                            margin-bottom: 12px;
                        }
                        .bookmark-link {
                            display: flex;
                            align-items: center;
                            font-size: 12px;
                            color: rgb(120, 119, 116);
                        }
                        .bookmark-favicon {
                            width: 16px;
                            height: 16px;
                            margin-right: 6px;
                            border-radius: 2px;
                            flex-shrink: 0;
                        }
                        .bookmark-url {
                            overflow: hidden;
                            text-overflow: ellipsis;
                            white-space: nowrap;
                        }

                        /* 이미지: 항상 오른쪽 끝 고정 */
                        .bookmark-media {
                            margin-left: auto;     /* 가장 오른쪽으로 밀기 */
                            flex: 0 0 180px;       /* 고정 폭 */
                            display: block;
                        }
                        .bookmark-image {
                            width: 180px;
                            height: 120px;
                            object-fit: cover;
                            background: #f1f1ef;
                            display: block;
                        }

                        /* 멀티라인 말줄임 (…): -webkit-line-clamp + 폴백 */
                        .clamp-2 {
                            display: -webkit-box;
                            -webkit-line-clamp: 2;
                            -webkit-box-orient: vertical;
                            overflow: hidden;

                            line-height: 1.3;
                            max-height: calc(1.3em * 2); /* 폴백 */
                        }
                        .clamp-2.body {
                            line-height: 1.4;
                            max-height: calc(1.4em * 2);
                        }
                        
                        .settings-btn {
                            display: inline-block;
                            padding: 12px 24px;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white !important;
                            text-decoration: none;
                            border-radius: 6px;
                            font-weight: 600;
                            font-size: 14px;
                            margin-bottom: 16px;
                            transition: opacity 0.2s;
                        }
                        .settings-btn:hover {
                            opacity: 0.9;
                        }

                        /* 반응형 */
                        @media (max-width: 850px) {
                            body { padding: 10px; }
                            .content { padding: 16px; }
                            .header { padding: 20px 16px; }
                            .greeting { padding: 16px; }
                            .bookmark-content { flex-direction: column !important; }
                            .bookmark-media {
                                margin-left: 0;
                                width: 100% !important;
                                flex: 0 0 auto;
                            }
                            .bookmark-image {
                                width: 100% !important;
                                height: 200px !important;
                            }
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>오늘의 아티클</h1>
                            <p>${today} (${dayOfWeekShort}) | 총 ${articles.size}개의 아티클</p>
                        </div>

                        <div class="content">
                            <div class="greeting">
                                <h2>안녕하세요, ${member.nickname}님!</h2>
                                <p>어제 하루 동안 게시된 아티클입니다. 각 아티클을 클릭하면 원문으로 이동합니다.</p>
                            </div>

                            <div class="articles-header">
                                아티클 목록
                            </div>

                            ${generateBookmarkCards(articles)}
                        </div>

                        <div class="footer" style="padding:24px;text-align:center;background:white;border-top:1px solid #e1e5e9;">
                            <a href="https://www.artijjaek.kr/setting?email=${member.email}&token=${member.uuidToken}" class="settings-btn">
                                ⚙️ 구독 설정
                            </a>
                            <p style="margin:0;color:rgb(120,119,116);font-size:12px;line-height:1.4;">
                                이 메일은 자동으로 발송되었습니다.<br>
                            </p>
                        </div>
                    </div>
                </body>
                </html>
            """.trimIndent()

            mimeMessageHelper.setText(content, true)
            javaMailSender.send(mimeMessage)
            log.info("메일 발송 성공!")
        } catch (e: Exception) {
            log.error("메일 발송 실패!", e)
            throw RuntimeException(e)
        }
    }

    fun generateBookmarkCards(articles: List<Article>): String {
        if (articles.isEmpty()) {
            return """
                <div style="padding: 40px; text-align: center; background: white; border-radius: 8px; border: 2px dashed #e1e5e9;">
                    <div style="font-size: 48px; margin-bottom: 16px;">📭</div>
                    <div style="font-size: 16px; color: rgb(120, 119, 116);">
                        어제 발행된 아티클이 없습니다.
                    </div>
                </div>
            """.trimIndent()
        }

        return articles.joinToString("\n") { article ->
            val safeLink = article.link.takeIf { it.isNotBlank() } ?: "#"
            val safeTitle = cleanText(article.title)
            val safeDescription = cleanText(article.description ?: "")

            val imageHtml = if (article.image?.isNotBlank() ?: false) {
                """
                <div class="bookmark-media">
                  <img src="${article.image}" alt="썸네일" class="bookmark-image">
                </div>
                """.trimIndent()
            } else {
                """
                """.trimIndent()
            }

            val logoHtml = if (article.company.logo.isNotBlank()) {
                """<img src="${article.company.logo}" alt="favicon" class="bookmark-favicon">"""
            } else {
                """<div class="bookmark-favicon" style="background:#e1e5e9;"></div>"""
            }

            """
            <a href="${safeLink}" class="bookmark-card">
                <div class="bookmark-content">
                    <div class="bookmark-text">
                        <div>
                            <div class="bookmark-title clamp-2">${safeTitle}</div>
                            <div class="bookmark-description clamp-2 body">${safeDescription}</div>
                            <div class="bookmark-link">
                                ${logoHtml}
                                <span class="bookmark-url">${article.company.nameKr}</span>
                                &nbsp;| ${article.link}
                            </div>
                        </div>
                    </div>
                    ${imageHtml} <!-- 항상 오른쪽 끝 -->
                </div>
            </a>
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

    fun sendSubscribeMail(member: Member) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val today = LocalDate.now()

        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")

            // 수신자/제목
            mimeMessageHelper.setTo(member.email)
            mimeMessageHelper.setFrom("noreply@artijjaek.kr", "아티짹")
            mimeMessageHelper.setReplyTo("noreply@artijjaek.kr")
            mimeMessageHelper.setSubject("[아티짹] 환영합니다 ${member.nickname}님!")

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
                                  ${member.nickname}님을 환영합니다!
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
                                              안녕하세요, ${member.nickname}님!
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
                                        <li style="margin:6px 0;">${member.nickname}님이 설정한 <strong>기업 블로그</strong>에서 <strong>선택한 카테고리</strong> 아티클이 올라오면 수집해요.</li>
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