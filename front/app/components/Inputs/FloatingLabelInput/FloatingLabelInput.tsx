import React from "react";

interface FloatingLabelInputProps {
    label: string;
    name: string;
    type?: string;
}

export default function FloatingLabelInput({
                                               label,
                                               name,
                                               type = "text",
                                           }: FloatingLabelInputProps) {
    return (
        <div className="relative group mb-6 bg-transparent w-full">
            <input
                type={type}
                name={name}
                id={name}
                placeholder=" "
                className="
          peer
          w-full
          px-3
          py-2
          text-gray-900
          bg-transparent
          dark:text-gray-200
          border-0
          border-b-2
          border-gray-300
          focus:outline-none
          transition-all
          duration-300
          ease-in-out
        "
            />
            <label
                htmlFor={name}
                className="
                            absolute
                            left-3
                            top-2
                            text-gray-500
                            cursor-text
                            transition-all
                            duration-300
                            ease-in-out

                            /* When the placeholder is visible (i.e., no input typed yet) */
                            peer-placeholder-shown:top-2
                            peer-placeholder-shown:text-base
                            peer-placeholder-shown:text-gray-500

                            /* While focused */
                            peer-focus:top-0
                            peer-focus:-translate-y-4
                            peer-focus:text-sm
                            peer-focus:text-blue-500

                            /* When the input is not empty (i.e., placeholder is hidden) */
                            peer-not-placeholder-shown:top-0
                            peer-not-placeholder-shown:-translate-y-4
                            peer-not-placeholder-shown:text-sm
                          "
            >
                {label}
            </label>


            {/*
        Animated bottom bar:
        - Centered horizontally at the container’s midpoint
        - Scale goes from 0 to 100% on focus
      */}
            <span
                className="
          pointer-events-none
          absolute
          bottom-0
          left-1/2
          h-[2px]
          w-full
          bg-blue-500
          transform
          -translate-x-1/2
          origin-center
          scale-x-0
          transition-transform
          duration-300
          ease-in-out
          group-focus-within:scale-x-100
        "
            />
        </div>
    );
}
