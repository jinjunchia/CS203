export default function LoginLayout({
	children,
}: Readonly<{
	children: React.ReactNode;
}>) {
	return (
		<div className="h-screen flex">
			{/* LEFT */}
			<div className="hidden bg-[url('/login.png')] bg-cover bg-center sm:block sm:w-[50%] relative">
				<div className="flex flex-col gap-8 text-white absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-3/4">
					<div className="h-12 w-12 bg-white rounded-3xl" />
					<h1 className="text-5xl font-bold">Level the Playing Field</h1>
					<p>
						Lorem ipsum dolor sit amet consectetur adipisicing elit. Pariatur
						doloribus dolore omnis eos! Voluptates pariatur exercitationem amet
						soluta ullam eaque! Quia quisquam animi maiores sunt consequuntur,
						ipsum deserunt placeat praesentium.
					</p>
				</div>
			</div>
			{/* RIGHT */}
			<div className="flex justify-center align-middle w-[100%] sm:w-[50%] lg:w-[50%] xl:w-[50%]">
				{children}
			</div>
		</div>
	);
}
