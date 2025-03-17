import axios, { AxiosResponse } from 'axios';

const post = async (url: string, payload): Promise<AxiosResponse> => {
    return await axios.post(url, payload);
}

export { post }
