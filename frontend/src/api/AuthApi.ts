import React from 'react';

const baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

export const GOOGLE_URL = `${baseURL}/oauth2/authorization/google`;
export const KAKAO_URL = `${baseURL}/oauth2/authorization/kakao`;