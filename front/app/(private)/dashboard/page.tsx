import React from "react";
import { cookies } from 'next/headers';
import axios, {AxiosResponse} from "axios";
import {BaseButton} from "../../components/Inputs/Buttons/BaseButton";
import { redirect } from "next/navigation";
import Link from 'next/link';

export const dynamic = "force-dynamic";

export default async function ImageProcessor({ searchParams }: { searchParams: { error?: string } }) {

    const { error } = await searchParams;
    const errorMessage = error ? error : null;

    async function handleForm(formData: FormData) {
        'use server'

        const imageFile = formData.get("image") as File;
        const normalizedIntensity = formData.get("normalizedIntensity");
        const effect = formData.get("effect");

        async function fileToBase64(file: File): Promise<string> {
            const arrayBuffer = await file.arrayBuffer();
            const bytes = new Uint8Array(arrayBuffer);

            let binary = '';
            for (let i = 0; i < bytes.byteLength; i++) {
                binary += String.fromCharCode(bytes[i]);
            }

            return `data:${file.type};base64,${btoa(binary)}`;
        }

        const base64Content = await fileToBase64(imageFile);

        const cookieStore = await cookies();

        const accessToken = cookieStore.get("accessToken")!.value;
        const refreshToken = cookieStore.get("refreshToken")!.value;

        const data = JSON.stringify({
            query: `mutation uploadImages($imagesOpts: [ImageOptionsRequestInput!]!) {
                      uploadImages(imagesOpts: $imagesOpts)
                }`,
            variables: {"imagesOpts":[{"base64Content":`${base64Content}`,
                    "imageFilters": effect,
                    "dimension": parseFloat(typeof normalizedIntensity === "string" ? normalizedIntensity : "1")
            }]}
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

        try {
            const response = await axios.request(config);

            if (response.data?.errors[0]?.message.includes("maximum quota reached")) {
                console.log("Maximum quota reached.")
                redirect(`http://localhost:3000/dashboard?error=Maximum%20quota%20reached.`);
            }
        } catch (err: AxiosResponse) {
            if (err.status === 401) {
                console.log(`refreshToken: ${refreshToken}`)
                const res = await axios.post(`http://192.168.49.2:30080/v1/auth/refresh?refreshToken=${refreshToken}`)
                const cookieStore = await cookies();

                cookieStore.set("accessToken", res.data.accessToken, {
                    httpOnly: true,
                    sameSite: "lax",
                    secure: false,
                });

                cookieStore.set("refreshToken", res.data.refreshToken, {
                    httpOnly: true,
                    sameSite: "lax",
                    secure: false,
                });

                config.headers = {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${res.data.accessToken}`
                };
                await axios.request(config);
            }
        }
    }

    return (
        <div className="container mx-auto p-6">
            <h1 className="text-2xl font-bold mb-6">Image Processor</h1>
            {errorMessage && (
                <div className="text-red-600 font-medium mb-4">
                    {errorMessage}
                </div>
            )}
            <form action={handleForm} method="post" encType="multipart/form-data" className="space-y-6">

                <div className="space-y-2">
                    <label htmlFor="image" className="block text-sm font-medium">
                        Select Image
                    </label>
                    <input
                        type="file"
                        id="image"
                        name="image"
                        accept="image/*"
                        className="block w-full text-sm border border-gray-300 rounded-md p-2"
                        required
                    />
                </div>

                <div className="space-y-2">
                    <label htmlFor="intensity" className="block text-sm font-medium">
                        Dimension: <span id="intensityValue">50%</span>
                    </label>
                    <input
                        type="range"
                        id="intensity"
                        name="intensity"
                        min="1"
                        max="100"
                        defaultValue="50"
                        className="w-full"
                    />
                    <input
                        type="hidden"
                        id="normalizedIntensity"
                        name="normalizedIntensity"
                        defaultValue="0.5"
                    />
                </div>

                <div className="space-y-2">
                    <label htmlFor="effect" className="block text-sm font-medium">
                        Select Effect
                    </label>
                    <select
                        id="effect"
                        name="effect"
                        className="block w-full rounded-md border border-gray-300 p-2"
                        required
                    >
                        <option value="GRAYSCALE">GRAYSCALE</option>
                    </select>
                </div>

                <div className="flex w-full items-center">
                    <BaseButton
                        type="submit"
                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                    >
                        Process Image
                    </BaseButton>

                    <Link href="/images" type="button" className="ml-auto">
                        <BaseButton
                            type="button"
                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                        >
                            See images
                        </BaseButton>
                    </Link>
                </div>
            </form>

            <script dangerouslySetInnerHTML={{
                __html: `
          document.getElementById('intensity').addEventListener('input', function(e) {
            const value = e.target.value;
            document.getElementById('intensityValue').textContent = value + '%';
            document.getElementById('normalizedIntensity').value = (parseInt(value) / 100).toFixed(2);
          });
        `
            }} />
        </div>
    );
}
