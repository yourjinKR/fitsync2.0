import apiClient from "./apiClient";

const BASE_URL = '/api/user'; 
  
const UserApi = {
  findAllUsers : () => apiClient.get(`${BASE_URL}/all`),
  createUser : (user : object) => apiClient.post(BASE_URL, user),
};

export default UserApi;