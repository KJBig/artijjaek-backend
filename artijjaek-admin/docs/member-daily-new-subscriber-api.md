# Admin API - 일자별 신규 구독자 수 조회

## 개요
- 신규 구독자 수를 날짜별로 집계하여 반환합니다.
- 그래프(라인/바 차트) 데이터 소스로 사용합니다.

## Endpoint
- `GET /admin/v1/member/subscriber/new/daily`

## Query Parameters
- `startDate` (필수, `yyyy-MM-dd`)
- `endDate` (필수, `yyyy-MM-dd`)

예시:
```http
GET /admin/v1/member/subscriber/new/daily?startDate=2026-02-01&endDate=2026-02-03
```

## Response
성공 시 `200 OK`

```json
{
  "isSuccess": true,
  "message": "요청성공",
  "data": [
    {
      "date": "2026-02-01",
      "subscriberCount": 3
    },
    {
      "date": "2026-02-02",
      "subscriberCount": 0
    },
    {
      "date": "2026-02-03",
      "subscriberCount": 1
    }
  ]
}
```

## 동작 규칙
- 집계 기준: `Member.createdAt`의 날짜(일 단위)
- 범위: `startDate` ~ `endDate` (양 끝 포함)
- 데이터가 없는 날짜도 `subscriberCount = 0`으로 포함하여 반환

## 에러
- 날짜 포맷이 `yyyy-MM-dd`가 아닌 경우:
  - `MethodArgumentTypeMismatchException`
- `startDate > endDate`인 경우:
  - 에러 코드: `REE-6`
  - 메시지: `요청값이 올바르지 않습니다.`
