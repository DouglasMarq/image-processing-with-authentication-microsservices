import React, {useEffect, useRef, useState} from "react";

interface AutoCompleteInputProps {
    label: string;
    name: string;
    suggestions: string[];
}

export default function AutoCompleteInput({
                                              label, name, suggestions,
                                          }: AutoCompleteInputProps) {
    const [inputValue, setInputValue] = useState("");
    const [filteredSuggestions, setFilteredSuggestions] = useState<string[]>([]);
    const [isOpen, setIsOpen] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    function handleFocus() {
        // Reopen suggestions if user refocuses and there is already text
        if (inputValue.trim() !== "") {
            setIsOpen(true);
        }
    }

    function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
        const {value} = event.target;
        setInputValue(value);

        if (value.trim() !== "") {
            const lowerValue = value.toLowerCase();
            const matched = suggestions.filter((s) => s.toLowerCase().startsWith(lowerValue));
            setFilteredSuggestions(matched);
            setIsOpen(true);
        } else {
            setFilteredSuggestions([]);
            setIsOpen(false);
        }
    }

    function handleSelect(suggestion: string) {
        setInputValue(suggestion);
        setIsOpen(false);
    }

    // Close suggestions if clicked outside
    useEffect(() => {
        function handleClickOutside(e: MouseEvent) {
            if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
                setIsOpen(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    return (<div className="relative w-full" ref={containerRef}>
            {/* Floating label wrapper */}
            <div className="relative group mb-6 bg-transparent w-full">
                <input
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
                    value={inputValue}
                    onChange={handleChange}
                    onFocus={handleFocus}
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

            /* If the input is not empty, keep label up */
            peer-not-placeholder-shown:top-0
            peer-not-placeholder-shown:-translate-y-4
            peer-not-placeholder-shown:text-sm
            peer-not-placeholder-shown:text-blue-500

            /* If focusing or typing, keep label up, too */
            peer-focus:top-0
            peer-focus:-translate-y-4
            peer-focus:text-sm
            peer-focus:text-blue-500
          "
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

            {/* Suggestions dropdown */}
            {isOpen && filteredSuggestions.length > 0 && (<ul
                    className="
            absolute
            z-10
            w-full
            bg-white
            border
            border-gray-300
            rounded-md
            max-h-60
            overflow-auto
            py-1
            shadow-md
          "
                >
                    {filteredSuggestions.map((suggestion) => (<li
                            key={suggestion}
                            className="
                px-3
                py-2
                text-gray-700
                hover:bg-blue-100
                cursor-pointer
              "
                            onClick={() => handleSelect(suggestion)}
                        >
                            {suggestion}
                        </li>))}
                </ul>)}
        </div>);
}
