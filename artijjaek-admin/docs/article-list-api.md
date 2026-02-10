# Admin Article List API

관리자 아티클 리스트 화면 연동용 API 명세입니다.

## Endpoint
- `GET /admin/v1/article/list`

## Auth
- Required
- Header: `Authorization: Bearer <accessToken>`

## Query Parameters
- `page` (`number`, optional): 페이지 번호 (0-base)
- `size` (`number`, optional): 페이지 크기
- `companyId` (`number`, optional): 회사 필터
- `categoryId` (`number`, optional): 카테고리 필터
- `title` (`string`, optional): 제목 검색어 (contains)
- `sortBy` (`string`, optional, default: `REGISTER_DATE`)
  - `REGISTER_DATE` | `TITLE` | `COMPANY` | `CATEGORY`
- `sortDirection` (`string`, optional, default: `DESC`)
  - `ASC` | `DESC`

## Sort Mapping (server internal)
- `REGISTER_DATE` -> `createdAt`
- `TITLE` -> `title`
- `COMPANY` -> `company.nameKr`
- `CATEGORY` -> `category.name`
- 모든 정렬은 tie-breaker로 `id`를 추가 적용

## Success Response (`200 OK`)
```json
{
  "isSuccess": true,
  "message": "요청성공",
  "data": {
    "pageNumber": 0,
    "totalCount": 12,
    "hasNext": true,
    "content": [
      {
        "articleId": 100,
        "title": "백엔드 개발자 채용",
        "company": {
          "companyId": 10,
          "companyNameKr": "회사A",
          "logo": "https://logo.example.com/company-a.png"
        },
        "categoryName": "백엔드",
        "link": "https://example.com/article/100",
        "image": "https://image.example.com/100.png",
        "description": "설명",
        "createdAt": "2025-01-10T12:00:00"
      }
    ]
  }
}
```

## Field Notes
- `data.pageNumber`: 현재 페이지 번호 (0-base)
- `data.totalCount`: 필터 조건 기준 전체 아티클 수
- `data.hasNext`: 다음 페이지 존재 여부
- `data.content[].company`: 회사명/로고 포함 객체
- `data.content[].categoryName`: 카테고리 미지정 시 `null`
- `data.content[].image`: 이미지가 없는 경우 `null`
- `data.content[].description`: 설명이 없는 경우 `null`

## Error Response Example
```json
{
  "isSuccess": false,
  "code": "REE-2",
  "message": "토큰이 존재하지 않습니다."
}
```

## Frontend TypeScript Types (권장)
```ts
export type ArticleListSortBy =
  | "REGISTER_DATE"
  | "TITLE"
  | "COMPANY"
  | "CATEGORY";

export type SortDirection = "ASC" | "DESC";

export interface ArticleCompanyResponse {
  companyId: number;
  companyNameKr: string;
  logo: string;
}

export interface ArticleSimpleResponse {
  articleId: number;
  title: string;
  company: ArticleCompanyResponse;
  categoryName: string | null;
  link: string;
  image: string | null;
  description: string | null;
  createdAt: string; // ISO-8601
}

export interface ArticleListPageResponse {
  pageNumber: number;
  totalCount: number;
  hasNext: boolean;
  content: ArticleSimpleResponse[];
}

export interface SuccessDataResponse<T> {
  isSuccess: boolean;
  message: string;
  data: T;
}

export interface ErrorResponse {
  isSuccess: false;
  code: string;
  message: string;
}
```

## Request Example
```bash
curl -G 'https://{host}/admin/v1/article/list' \
  -H 'Authorization: Bearer {accessToken}' \
  --data-urlencode 'page=0' \
  --data-urlencode 'size=20' \
  --data-urlencode 'companyId=10' \
  --data-urlencode 'categoryId=20' \
  --data-urlencode 'title=개발자' \
  --data-urlencode 'sortBy=REGISTER_DATE' \
  --data-urlencode 'sortDirection=DESC'
```
