'use client'
import React from "react";
import AutoCompleteInput from "@/app/components/Inputs/AutoCompleteInput";
import { BaseButton } from "@/app/components/Inputs/Buttons/BaseButton";
import { ButtonDanger } from "@/app/components/Inputs/Buttons/ButtonDanger";
import Menu from "@/app/components/NavigatonBar/Menu/Menu";

export default function SamplePage() {
    return (
        <div>
            <Menu />
            <div className="p-4 w-56">
                <AutoCompleteInput
                    label="Type here"
                    name="autoCompleteInput"
                    suggestions={["Cat", "Car", "Cactus", "Camera", "Carbon"]}
                />
                <BaseButton
                    onClick={() => console.log('Base button clicked!')}
                >
                    Normal Button
                </BaseButton>

                <ButtonDanger
                    onClick={() => console.log('Danger button clicked!')}
                >
                    Danger Action
                </ButtonDanger>

                <ButtonDanger
                    icon={<span>!</span>}
                    onClick={() => console.log('Icon Danger button!')}
                >
                    With Icon
                </ButtonDanger>
            </div>
        </div>
);
}
