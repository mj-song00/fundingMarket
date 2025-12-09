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
};

const BASE_URL = 'http://localhost:8080';
const TOTAL_PROJECTS = 154;

function getRandomProjectId(){
    return Math.floor(Math.random() * TOTAL_PROJECTS)+1;
}

export default function(){
    const projectId = getRandomProjectId();
    const url =  `${BASE_URL}/api/v1/project/${projectId}`;
    const res = http.get(url);
    check(res, {
        [`GET ${url} 상태코드가 200`]: (r) => r.status === 200,
        [`GET ${url} 응답에 성공 메시지가 있음`]: (r) =>
            r.json().message && r.json().message.includes('조회 완료'),
    });

    sleep(1);
}