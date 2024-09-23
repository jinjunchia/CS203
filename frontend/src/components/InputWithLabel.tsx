"use client";

import React, { useState } from "react";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { FaRegEye, FaRegEyeSlash } from "react-icons/fa";

interface InputWithLabelProps {
	label: string;
	placeholder?: string;
	htmlFor: string;
	type: React.HTMLInputTypeAttribute;
}

const InputWithLabel: React.FC<InputWithLabelProps> = ({
	label,
	type,
	placeholder,
	htmlFor,
}: InputWithLabelProps) => {
	const [inputType, setInputType] = useState<string>(type);

	const togglePasswordVisibility = () => {
		setInputType((prevType) => (prevType === "password" ? "text" : "password"));
	};

	const isPassword = type === "password";

	return (
		<div className="grid w-full items-center gap-1.5 relative">
			<Label htmlFor={htmlFor}>{label}</Label>
			<Input
				type={inputType}
				id={htmlFor}
				placeholder={placeholder}
				className={isPassword ? "pr-10" : undefined}
			/>
			{isPassword && (
				<button
					type="button"
					onClick={togglePasswordVisibility}
					className="absolute right-3 top-10 transform -translate-y-1/2 text-gray-500 hover:text-gray-700 focus:outline-none"
					aria-label={
						inputType === "password" ? "Show password" : "Hide password"
					}
				>
					{inputType === "password" ? (
						<FaRegEye className="h-5 w-5" />
					) : (
						<FaRegEyeSlash className="h-5 w-5" />
					)}
				</button>
			)}
		</div>
	);
};

export default InputWithLabel;
