# OneManChallenge_BE
원맨 IT 뉴스 Backend Repo
- 30만건 이상의 IT 관련  뉴스를 빠르게 검색/조회하는 서비스

## 1. 기술스택
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=Spring Boot&logoColor=white"> <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"> 

<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">

<img src="https://img.shields.io/badge/Elasticsearch-005571?style=for-the-badge&logo=Elasticsearch&logoColor=white"> <img src="https://img.shields.io/badge/Logstash-005571?style=for-the-badge&logo=Logstash&logoColor=white">

<img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"> 
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=for-the-badge&logo=Amazon EC2&logoColor=white">

<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
 

## 2. 구성도
![image](https://user-images.githubusercontent.com/31820402/224474232-894aeac9-32b1-4f04-9b33-a7b82e288fe9.png)

## 3. 대표기능 소개
### 3-1. Mysql Replication(이중화)
#### 구현 이유
1) 평소 구현해보고 싶었던 기능
2) 백업
> - 단일 DB서버의 경우 장애발생에 대한 우려
> - 유사시 복구 및 빠른 대응을 위한 준비의 필요성 인지
 3) 읽기와 쓰기를 따로 처리하는 ```CQRS패턴``` 적용

![image](https://user-images.githubusercontent.com/31820402/224474633-92732c26-2045-419b-9d74-f2a6b8c36ff6.png)

#### 느낀점
- 기능 구현 전에는 RDBMS의 data를 Disk I/O 처리하는 것으로 예상
- 실제 구현해보니 Binary log 내용 안의 sql문을 실행시키며 동기화 진행
- 과거 학습했던 Oracle Redo Log File과 흡사한 방식으로 진행되는 공통점 발견
- 이를 통해 Disk I/O와 ```Memory I/O```에 대한 인지와 성능 개선을 위해 ```Cache``` 사용 이유를 체감 
- 하지만 이런 장점을 악용한 ```Memory 해킹```에 대한 위협도 인지

- - -
