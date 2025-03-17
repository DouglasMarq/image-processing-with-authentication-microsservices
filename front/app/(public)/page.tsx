import React from "react";
import FloatingLabelInput from "@/app/components/Inputs/FloatingLabelInput/FloatingLabelInput";
import {BaseButton} from "@/app/components/Inputs/Buttons/BaseButton";
import { redirect } from 'next/navigation';
import { ToastContainer, toast, Bounce } from 'react-toastify';
import { AxiosResponse } from "axios";
import { cookies } from "next/headers";
import { post } from "../lib/axios";

async function handleLogin(formData: FormData) {
    "use server"
    const payload = {
        email: formData.get('email') as string,
        password: formData.get('password') as string,
    };

    try {
        const res: AxiosResponse = await post('http://192.168.49.2:30080/v1/auth/login', payload)

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

    } catch (error: AxiosResponse) {
        if (error.status === 401 || error.status === 403) toast.error('Incorrect Password or user does not exist.');
    }

    // Your login logic, e.g.:
    // if (!isValidUser(email, password)) {
    //   throw new Error('Invalid email or password');
    // }
    // If successful, you could redirect somewhere:
    // redirect('/dashboard');

    console.log('Logging in user:', payload.email);
    console.log('Logging in user:', payload.password);

    redirect('/dashboard');
}

async function goToRegister() {
    "use server"
    redirect('/register');
}

export default function Home() {
    return (
        <div className="flex items-center justify-center h-screen bg-gray-950">
            <ToastContainer
                position="top-center"
                autoClose={5000}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick={false}
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme="dark"
                transition={Bounce}
            />
            <div className="bg-gray-900 p-6 rounded shadow-md max-w-sm w-full">
                <h1 className="text-2xl font-bold text-white mb-8 text-center">Login</h1>
                <form>
                    <FloatingLabelInput
                        type="text"
                        label="Email"
                        name="email"
                    />

                    <FloatingLabelInput
                        type="password"
                        label="Password"
                        name="password"
                    />

                    <div className="flex w-full items-center justify-between">
                        <BaseButton
                            formAction={handleLogin}
                            type="submit"
                            className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
                        >
                            Login
                        </BaseButton>

                        <BaseButton
                            formAction={goToRegister}
                            type="submit"
                            className="px-4 py-2 rounded bg-gray-500 text-white hover:bg-gray-600"
                        >
                            Sign Up
                        </BaseButton>
                    </div>
                </form>
            </div>
        </div>
    );
}
