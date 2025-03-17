'use client'
import React from 'react';
import { redirect } from 'next/navigation';
import { FaArrowLeft } from "react-icons/fa6";
import FloatingLabelInput from "@/app/components/Inputs/FloatingLabelInput/FloatingLabelInput";
import {BaseButton} from "@/app/components/Inputs/Buttons/BaseButton";
import { ToastContainer, toast, Bounce } from 'react-toastify';
import { post } from '../../lib/axios';
import {AxiosResponse} from "axios";

export default function Register() {
    function handleRegister(formData: FormData) {

        const payload = {
            name: formData.get('name') as string,
            email: formData.get('email') as string,
            password: formData.get('password') as string,
            confirmPassword: formData.get('confirmPassword') as string
        };

        if (payload.password !== payload.confirmPassword) {
            throw new Error('Passwords do not match.');
        }

        post('http://192.168.49.2:30080/v1/auth/login', payload)
        .then((res: AxiosResponse) => {
            if (res.status === 200) redirect('/');
        }).catch(error => {
            if (error.status === 401 || error.status === 403) toast.error('Incorrect Password or user does not exist.');
        });

        // TODO: Check user existence, hash password, then save user in DB, etc.
        console.log('Username:', payload.name);
        console.log('Email:', payload.email);
        console.log('Password:', payload.password);
        console.log('confirmPassword:', payload.confirmPassword);

        // If successful, possibly redirect somewhere:
    }

    // Separate server action just to go back to the login page
    function goBack() {
        redirect('/');
    }

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
                <div className="flex items-center mb-4">
                    <form>
                        <button
                            formAction={goBack}
                            type="submit"
                            className="flex items-center text-white"
                        >
                            <FaArrowLeft className="mr-2" />
                            <h1 className="text-2xl font-bold">Register</h1>
                        </button>
                    </form>
                </div>

                <form action={handleRegister}>
                    <FloatingLabelInput
                    label="Name"
                    name="name"
                    type="text"
                    />

                    <FloatingLabelInput
                        label="E-mail"
                        type="email"
                        name="email"
                    />

                    <FloatingLabelInput
                        label="Password"
                        type="password"
                        name="password"
                    />

                    <FloatingLabelInput
                        label="Confirm Password"
                        type="password"
                        name="confirmPassword"
                    />

                    <div className="flex justify-center items-center mt-3.5">
                        <BaseButton
                            type="submit"
                            className="w-full"
                        >
                            Submit
                        </BaseButton>

                    </div>

                </form>
            </div>
        </div>
    );
}
