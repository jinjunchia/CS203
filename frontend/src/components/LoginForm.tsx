"use client";

import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import {
	Form,
	FormControl,
	FormField,
	FormItem,
	FormLabel,
	FormMessage,
} from "@/components/ui/form";
import { Input } from "./ui/input";
import { useToast } from "@/hooks/use-toast";
import { ToastAction } from "./ui/toast";
import useAuth from "@/hooks/useAuth";
import { useRouter } from "next/navigation";

const formSchema = z.object({
	username: z.string().min(1, {
		message: "Please input your username.",
	}),
	password: z.string().min(1, {
		message: "Please input your password.",
	}),
});

const LoginForm = () => {
	const router = useRouter();
	const { login, user } = useAuth();
	const { toast } = useToast();
	const [loading, setLoading] = useState(false);

	const form = useForm<z.infer<typeof formSchema>>({
		resolver: zodResolver(formSchema),
		defaultValues: {
			username: "",
			password: "",
		},
	});

	const onSubmit = async (values: z.infer<typeof formSchema>) => {
		console.log(values);

		setLoading(true);

		try {
			await login(values.username, values.password);
		} catch (err) {
			toast({
				variant: "destructive",
				title: "Uh oh! Something went wrong.",
				description: "Please check your connection.",
				action: <ToastAction altText="Try again">Try again</ToastAction>,
			});
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		console.log(user);
		if (user) {
			router.push("/admin");
		}
	}, [user, router]);

	return (
		<Form {...form}>
			<form onSubmit={form.handleSubmit(onSubmit)}>
				<div className="flex flex-col gap-6 ">
					<FormField
						control={form.control}
						name="username"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Username</FormLabel>
								<FormControl>
									<Input placeholder="" {...field} />
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
					<FormField
						control={form.control}
						name="password"
						render={({ field }) => (
							<FormItem>
								<FormLabel>Password</FormLabel>
								<FormControl>
									<Input placeholder="" {...field} type="password" />
								</FormControl>
								<FormMessage />
							</FormItem>
						)}
					/>
				</div>
				<div className="flex justify-end align-top my-3">
					<button
						type="button"
						className="underline text-xs"
						onClick={() => router.push("/forgetpassword")}
					>
						Forgot your password?
					</button>
				</div>

				<Button
					type="submit"
					disabled={loading}
					className="hover:bg-lamaSky hover:text-gray-600"
				>
					{loading ? "Logging in..." : "Login"}
				</Button>
			</form>
		</Form>
	);
};

export default LoginForm;
