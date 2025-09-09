import React from 'react';
import apiClient from './apiClient';

const baseURL = process.env.REACT_APP_API_URL || '';

const LOGIN_LINK = {
  google : `${baseURL}/oauth2/authorization/google`,
}

export default LOGIN_LINK;