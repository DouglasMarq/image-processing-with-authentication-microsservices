import React from 'react';
import { BaseButton } from './BaseButton';

interface ButtonDangerProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    icon?: React.ReactNode;
    children: React.ReactNode;
}

export function ButtonDanger({ children, icon, className = '', ...props }: ButtonDangerProps) {
    return (
        <BaseButton
            {...props}
            icon={icon}
            className={`
        bg-red-600
        hover:brightness-110
        active:scale-95
        ${className}
      `}
        >
            {children}
        </BaseButton>
    );
}
