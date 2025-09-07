package com.noati.batch.service

import com.noati.core.domain.Article
import com.noati.core.domain.Member
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MailService(
    private val javaMailSender: JavaMailSender,
) {

    private val log = org.slf4j.LoggerFactory.getLogger(MailService::class.java)

    fun sendMail(member: Member, articles: List<Article>) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val today = LocalDate.now()

        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, false, "UTF-8")

            // 메일을 받을 수신자 설정
            mimeMessageHelper.setTo(member.email)
            // 메일의 제목 설정
            mimeMessageHelper.setSubject("[${today}] 아티클 목록")

            // html 문법 적용한 메일의 내용
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
                            max-width: 600px;
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
                            font-weight: 600;
                        }
                        
                        /* 북마크 카드 스타일 */
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
                            min-height: 120px;
                        }
                        .bookmark-text {
                            flex: 1;
                            padding: 16px;
                            display: flex;
                            flex-direction: column;
                            justify-content: space-between;
                        }
                        .bookmark-title {
                            font-size: 16px;
                            font-weight: 600;
                            color: rgb(55, 53, 47);
                            line-height: 1.3;
                            margin-bottom: 8px;
                            display: -webkit-box;
                            -webkit-line-clamp: 2;
                            -webkit-box-orient: vertical;
                            overflow: hidden;
                        }
                        .bookmark-description {
                            font-size: 14px;
                            color: rgb(120, 119, 116);
                            line-height: 1.4;
                            margin-bottom: 12px;
                            display: -webkit-box;
                            -webkit-line-clamp: 2;
                            -webkit-box-orient: vertical;
                            overflow: hidden;
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
                        .bookmark-image {
                            width: 180px;
                            height: 120px;
                            object-fit: cover;
                            flex-shrink: 0;
                            background: #f1f1ef;
                        }
                        
                        .footer {
                            padding: 24px;
                            text-align: center;
                            background: white;
                            border-top: 1px solid #e1e5e9;
                        }
                        .footer p {
                            margin: 0;
                            color: rgb(120, 119, 116);
                            font-size: 12px;
                            line-height: 1.4;
                        }
                        .footer a {
                            color: #667eea;
                            text-decoration: none;
                        }
                        
                        /* 반응형 디자인 */
                        @media (max-width: 850px) {  /* 800px + 여유공간 50px */
                            body { padding: 10px; }
                            .content { padding: 16px; }
                            .header { padding: 20px 16px; }
                            .greeting { padding: 16px; }
                            .bookmark-content { 
                                flex-direction: column !important; 
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
                        <!-- 헤더 섹션 -->
                        <div class="header">
                            <h1>📚 오늘의 아티클</h1>
                            <p>${today} • 총 ${articles.size}개의 아티클</p>
                        </div>
                        
                        <!-- 콘텐츠 섹션 -->
                        <div class="content">
                            <!-- 인사말 -->
                            <div class="greeting">
                                <h2>안녕하세요, ${member.email}님! 👋</h2>
                                <p>오늘 선별된 흥미로운 아티클들을 준비했습니다. 각 아티클을 클릭하면 원문으로 이동합니다.</p>
                            </div>
                            
                            <!-- 아티클 목록 헤더 -->
                            <div class="articles-header">
                                📖 추천 아티클 목록
                            </div>
                            
                            <!-- 북마크 카드들 -->
                            ${generateBookmarkCards(articles)}
                        </div>
                        
                        <!-- 푸터 섹션 -->
                        <div class="footer">
                            <p>
                                이 메일은 자동으로 발송되었습니다.<br>
                                문의사항이 있으시면 <a href="mailto:support@example.com">support@example.com</a>으로 연락주세요.
                            </p>
                            <p style="margin-top: 12px; font-size: 11px;">
                                © 2024 Your Company Name. All rights reserved.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
            """.trimIndent()

            // 메일의 내용 설정
            mimeMessageHelper.setText(content, true)

            javaMailSender.send(mimeMessage)

            log.info("메일 발송 성공!")
        } catch (e: Exception) {
            log.error("메일 발송 실패!", e) // log.equals -> log.error로 수정
            throw RuntimeException(e)
        }
    }

    fun generateBookmarkCards(articles: List<Article>): String {
        if (articles.isEmpty()) {
            return """
                <div style="padding: 40px; text-align: center; background: white; border-radius: 8px; 
                           border: 2px dashed #e1e5e9;">
                    <div style="font-size: 48px; margin-bottom: 16px;">📭</div>
                    <div style="font-size: 16px; color: rgb(120, 119, 116);">
                        오늘은 추천할 아티클이 없습니다.
                    </div>
                </div>
            """.trimIndent()
        }

        return articles.mapIndexed { index, article ->
            // URL 안전하게 처리
            val safeLink = article.link?.takeIf { it.isNotBlank() } ?: "#"

            // 텍스트 안전하게 처리 (HTML 이스케이핑)
            val safeTitle = cleanText(article.title ?: "제목 없음")
            val safeDescription = cleanText(article.description ?: "설명이 없습니다.")

            // 이미지 처리
            val imageHtml = if (!article.image.isNullOrBlank()) {
                """<img src="${article.image}" alt="썸네일" class="bookmark-image" style="width: 180px; height: 120px; object-fit: cover; background: #f1f1ef;">"""
            } else {
                """<div style="width: 180px; height: 120px; background: linear-gradient(135deg, #f0f2f5 0%, #e1e5e9 100%); display: flex; align-items: center; justify-content: center;">
                       <div style="font-size: 24px; color: rgb(120, 119, 116);">📄</div>
                   </div>"""
            }

            // 회사 로고 처리
            val logoHtml = if (!article.company?.logo.isNullOrBlank()) {
                """<img src="${article.company?.logo}" alt="favicon" class="bookmark-favicon" style="width: 16px; height: 16px; margin-right: 6px; border-radius: 2px;">"""
            } else {
                """<div style="width: 16px; height: 16px; margin-right: 6px; background: #e1e5e9; border-radius: 2px;"></div>"""
            }

            """
            <a href="${safeLink}" class="bookmark-card" style="display: block; text-decoration: none; color: inherit; 
               background: white; border-radius: 8px; overflow: hidden; 
               box-shadow: rgba(15, 15, 15, 0.1) 0px 0px 0px 1px, rgba(15, 15, 15, 0.1) 0px 2px 4px;
               margin-bottom: 16px;">
                
                <div class="bookmark-content" style="display: flex; min-height: 120px; position: relative;">
                    <div class="bookmark-text" style="flex: 1; padding: 16px; padding-right: 200px; display: flex; flex-direction: column; justify-content: flex-start;">
                        <div class="bookmark-title" style="font-size: 16px; font-weight: 600; color: rgb(55, 53, 47); 
                             line-height: 1.3; margin-bottom: 8px;">
                            ${safeTitle}
                        </div>
                        <div class="bookmark-description" style="font-size: 14px; color: rgb(120, 119, 116); 
                             line-height: 1.4; margin-bottom: 12px;">
                            ${safeDescription}
                        </div>
                        <div class="bookmark-link" style="display: flex; align-items: center; font-size: 12px; color: rgb(120, 119, 116); margin-top: auto;">
                            ${logoHtml}
                            <span class="bookmark-url" style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                ${article.company?.nameKr ?: "알 수 없는 사이트"}
                            </span>
                        </div>
                    </div>
                    ${imageHtml}
                </div>
            </a>
            """.trimIndent()
        }.joinToString("\n")
    }

    // HTML 텍스트를 안전하게 처리하는 함수
    private fun cleanText(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .trim()
            .take(200) // 최대 200자로 제한
    }
}