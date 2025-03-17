import React from 'react';

interface BaseButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    icon?: React.ReactNode;
    children: React.ReactNode;
}

export function BaseButton({ children, icon, className = '', ...props }: BaseButtonProps) {
    return (
        <button
            {...props}
            className={`
        px-4 py-2 rounded-2xl shadow-md
        transform-gpu transition-all duration-150 ease-in-out
        bg-blue-600 text-white
        hover:brightness-110
        active:scale-95
        ${className}
      `}
        >
            {icon && <span className="mr-2">{icon}</span>}
            {children}
        </button>
    );
}
