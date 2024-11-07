"use client";
import Link from "next/link";
import LoginForm from "@/components/LoginForm";

export default function RegisterPage() {
  return (
    <div className="w-screen h-screen bg-cover bg-center flex justify-center items-center">
      {/* White Box (Taller with Enhanced Shadow) */}
      <div className="bg-white py-16 px-8 rounded-xl shadow-2xl w-8/12 max-w-2xl min-h-[500px]">
        {/* Heading */}
        <h1 className="text-center text-bold text-xl mb-8">I want to be ...</h1>

        {/* Buttons for Create Admin and Create Player - in a row */}
        <div className="flex space-x-8 mb-6 justify-center">
          <Link href="/register/admin">
            <button className="w-full py-14 px-16 text-2xl bg-black text-white rounded-md hover:bg-lamaSky hover:text-gray-600">
              Admin
            </button>
          </Link>
          <Link href="/register/player">
            <button className="w-full py-14 px-16 text-2xl bg-black text-white rounded-md hover:bg-lamaSky hover:text-gray-600">
              Player
            </button>
          </Link>
        </div>

        {/* Already have an Account Link */}
        <div className="text-center">
          <p className="text-bold text-sm">
            Already have an account?{" "}
            <Link href="/login" className="underline">
              Click here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
