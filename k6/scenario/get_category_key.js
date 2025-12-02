import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 50 }, // 1분 동안 50명의 사용자로 증가
        { duration: '3m', target: 100 }, // 3분 동안 100명의 사용자 유지
        { duration: '1m', target: 0 }, // 1분 동안 사용자 감소
    ],
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    const categories = ['GAME', 'HOME', 'LIVING', 'ART', 'PET', 'WEBTOON', 'GOODS',
        'CLOTHES', 'TECH', 'PERFUME', 'MUSIC', 'BAG', 'BEAUTY', 'ANIME' ];

    const category = categories[Math.floor(Math.random() * categories.length)];
    const url = `${BASE_URL}/api/v1/project/category/${category}`;
    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}