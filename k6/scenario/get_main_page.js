import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    vus: 260,
    stages: [
        { duration: '2m', target: 160 }, // 2분 동안 50명의 사용자로 증가
        { duration: '8m', target: 300 }, // 3분 동안 100명의 사용자 유지
        { duration: '3m', target: 300} ,
        { duration: '2m', target: 0 }, // 1분 동안 사용자 감소
    ],
    // 추가 옵션: 임계값 설정 (Thresholds)
    thresholds: {
        // 응답 시간 (p95: 95%의 요청이 500ms 이내에 완료되어야 함)
        'http_req_duration': ['p(95)<500'],
        // 실패율 (모든 요청의 실패율이 1% 미만이어야 함)
        'checks': ['rate>0.99'],
    },
};

const BASE_URL = 'http://localhost:8080';

export default function(){
    const url = `${BASE_URL}/api/v1/project`;
    const res = http.get(url);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
    sleep(Math.random() * (1.5 - 0.5) + 0.5);
}