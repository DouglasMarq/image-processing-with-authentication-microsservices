import React, { useMemo } from 'react';
import {BaseButton} from "@/app/components/Inputs/Buttons/BaseButton";

/**
 * Utility function to derive initials from a user's name.
 */
function getUserInitials(name: string) {
    const parts = name.trim().split(/\s+/);
    const firstInitial = parts[0]?.[0]?.toUpperCase() ?? '';
    const lastInitial = parts.length > 1 ? parts[parts.length - 1][0]?.toUpperCase() : '';
    return firstInitial + lastInitial;
}

/**
 * Generates a deterministic RGB color based on a string (e.g., the user's name).
 * This way, SSR and client will match and avoid hydration issues.
 */
function getColorFromString(input: string) {
    let hash = 0;
    for (let i = 0; i < input.length; i += 1) {
        hash = input.charCodeAt(i) + ((hash << 5) - hash);
    }
    // Convert hash to valid RGB channels
    const r = (hash >> 16) & 255;
    const g = (hash >> 8) & 255;
    const b = hash & 255;
    return `rgb(${r}, ${g}, ${b})`;
}

interface MenuProps {
    userName?: string;
}

export default function Menu({ userName = 'Douglas Marques' }: MenuProps) {
    const userInitials = getUserInitials(userName);

    // Color is stable (deterministic) for each userName, avoiding mismatches.
    const stableColor = useMemo(() => getColorFromString(userName), [userName]);

    return (
        <nav className="flex items-center bg-white shadow sticky top-0 w-full p-2">
            {/* Left: Logo */}
            <div className="flex items-center">
                <div className="text-xl font-bold text-black">Front for Image Service</div>
                <div className="flex-shrink-0 border-l border-gray-300 ml-4 h-8" />
            </div>

            {/* Center: Buttons, etc. */}
            <div className="flex-1 flex items-center justify-center space-x-4">
                <BaseButton className="px-3 py-1">Button</BaseButton>
                <BaseButton className="px-3 py-1">Dropdown</BaseButton>
            </div>

            {/* Right: User icon */}
            <div
                className="w-8 h-8 rounded-full flex items-center justify-center text-white font-bold ml-4"
                style={{ backgroundColor: stableColor }}
            >
                {userInitials}
            </div>
        </nav>
    );
}
