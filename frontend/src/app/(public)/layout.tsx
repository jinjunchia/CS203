import PublicNavbar from "@/components/PublicNavbar";

export default function PublicLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return (
		<div className="h-screen w-5/6 mx-auto">
			<PublicNavbar />

			{children}
		</div>
	);
}
