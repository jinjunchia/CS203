import Head from 'next/head';

export default function RegisterLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <>
      <Head>
        <title>Register Page</title>
      </Head>
      {/* Independent Layout for Register */}
      <div className="bg-[url('/login.png')] bg-no-repeat bg-center bg-fixed bg-cover w-screen h-screen flex justify-center items-center">
        {children}
      </div>
    </>
  );
}