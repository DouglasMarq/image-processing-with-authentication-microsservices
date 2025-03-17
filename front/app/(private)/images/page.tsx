import {BaseButton} from "@/app/components/Inputs/Buttons/BaseButton";
import Link from "next/link";
import React, {use} from "react";
import {cookies} from "next/headers";
import axios, {AxiosResponse} from "axios";

export default async function Images() {
    const cookieStore = await cookies();

    const accessToken = cookieStore.get("accessToken")!.value;
    const refreshToken = cookieStore.get("refreshToken")!.value;

    const data = JSON.stringify({
        query: `query  {
              getImages {
                url
              }
            }`,
        variables: {}
    });

    const config = {
        method: 'post',
        maxBodyLength: Infinity,
        url: 'http://192.168.49.2:30081/graphql',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}`
        },
        data : data
    };
    let response;

    try {
        response = await axios.request(config);

        console.log(`aaaaaaaaaaaaaa:::: ${JSON.stringify(response.data)}`);
    } catch (err: AxiosResponse) {
        // if (err.response?.status === 401) {
        //     console.log(`refreshToken: ${refreshToken}`)
        // }
    }


    return (
        <div>
            <Link href="/dashboard" type="button" className="ml-auto">
                <BaseButton
                    type="button"
                    className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                >
                    Go Back
                </BaseButton>
            </Link>

            <div style={{display: "flex", flexWrap: "wrap", justifyContent: "space-evenly"}}>
                {
                    response?.data?.data?.getImages?.map((image: any) => (
                        <img src={image.url} alt="image"
                             className="w-auto h-auto object-contain"
                        />
                    ))
                }
            </div>
        </div>
    );
}
