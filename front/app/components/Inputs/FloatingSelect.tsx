import React from "react";

interface FloatingSelectProps {
    label: string;
    name: string;
    options: string[];
    value: string;
    onChange: (newValue: string) => void;
    style?: React.CSSProperties; // Allows inline styles
}

export default function FloatingSelect({
                                           label,
                                           name,
                                           options,
                                           value,
                                           onChange,
                                           style,
                                       }: FloatingSelectProps) {
    const hasValue = value.trim() !== "";

    return (
        <div className="relative group mb-6 bg-transparent w-full" style={style}>
            <select
                id={name}
                name={name}
                className={`
                      peer
                      w-full
                      px-3
                      py-2
                      bg-transparent
                      border-0
                      border-b-2
                      border-gray-300
                      focus:outline-none
                      transition-all
                      duration-300
                      ease-in-out
                `}
                style={{ color: style?.color }}
                value={value}
                onChange={(e) => onChange(e.target.value)}
            >
                <option value="" disabled>
                    {label}
                </option>
                {options.map((option) => (
                    <option key={option} value={option}>
                        {option}
                    </option>
                ))}
            </select>

            <label
                htmlFor={name}
                className={`
          absolute
          left-3
          cursor-text
          transition-all
          duration-300
          ease-in-out
          ${hasValue ? "top-0 -translate-y-4 text-sm" : "top-2"}
          peer-focus:top-0
          peer-focus:-translate-y-4
          peer-focus:text-sm
        `}
                style={{ color: style?.color }}
            >
                {label}
            </label>

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
