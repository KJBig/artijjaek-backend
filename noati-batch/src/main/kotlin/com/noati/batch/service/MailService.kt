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
                    <html xmlns:th="http://www.thymeleaf.org">

                    <body>
                    <div style="margin:100px;">
                        <h1> 테스트 메일 </h1>
                        <br>

                        <div align="center" style="border:1px solid black; padding: 20px;">
                            <h3> 테스트 메일 내용 </h3>
                            <br>
                            
                            <!-- 노션 스타일 북마크 카드 -->
                            <div style="margin: 20px 0;">
                                <a href="https://oliveyoung.tech/2025-09-04/article-editor/" 
                                   target="_blank" 
                                   style="text-decoration: none; color: inherit;">
                                    <div style="border: 1px solid #e1e5e9; 
                                               border-radius: 8px; 
                                               padding: 16px; 
                                               background-color: #ffffff; 
                                               box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); 
                                               transition: box-shadow 0.2s ease;
                                               max-width: 600px;
                                               display: flex;
                                               align-items: flex-start;
                                               gap: 16px;">
                                        
                                        <!-- 왼쪽 텍스트 영역 -->
                                        <div style="flex: 1; min-width: 0;">
                                            <h4 style="margin: 0 0 8px 0; 
                                                       font-size: 16px; 
                                                       font-weight: 600; 
                                                       color: #2d3748; 
                                                       line-height: 1.4;">
                                                올리브영 기술 블로그 - Article Editor
                                            </h4>
                                            <p style="margin: 0 0 8px 0; 
                                                      font-size: 14px; 
                                                      color: #718096; 
                                                      line-height: 1.4;">
                                                동네에서 만나온 모임팀이 꿈꾸는 새로운 라이프스타일 | 당근 블로그<br>
                                                당근 팀이 일하는 문화와 방식을 소개해요.
                                            </p>
                                            <div style="display: flex; 
                                                        align-items: center; 
                                                        font-size: 12px; 
                                                        color: #a0aec0;">
                                                <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='%23a0aec0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cpath d='m4 4 16 16M4 20 20 4'%3E%3C/path%3E%3C/g%3E%3C/svg%3E" 
                                                     style="width: 16px; height: 16px; margin-right: 6px;" alt="link">
                                                oliveyoung.tech
                                            </div>
                                        </div>
                                        
                                        <!-- 오른쪽 이미지 영역 (썸네일) -->
                                        <div style="flex-shrink: 0; 
                                                    width: 120px; 
                                                    height: 80px; 
                                                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); 
                                                    border-radius: 6px; 
                                                    display: flex; 
                                                    align-items: center; 
                                                    justify-content: center;">
                                            <span style="color: white; 
                                                         font-size: 24px; 
                                                         font-weight: bold;">
                                                📝
                                            </span>
                                        </div>
                                    </div>
                                </a>
                            </div>
                        </div>
                        <br/>
                    </div>

                    </body>
                    </html>

                    """.trimIndent()


            // 메일의 내용 설정
            mimeMessageHelper.setText(content, true)

            javaMailSender.send(mimeMessage)

            log.info("메일 발송 성공!")
        } catch (e: Exception) {
            log.equals("메일 발송 실패!")
            throw RuntimeException(e)
        }
    }
}