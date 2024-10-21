"use client";
import Link from "next/link";

export default function RegisterAdminPage() {
  return (
    <div className="w-screen h-screen bg-cover bg-center flex justify-center items-center">
      {/* White Box (Taller with Enhanced Shadow) */}
      <div className="bg-white py-16 px-8 rounded-xl shadow-2xl w-8/12 max-w-2xl min-h-[850px]">
        {/* Heading */}
        <h1 className="text-center text-3xl mb-8">Create Admin Page</h1>

        {/* Form */}
        <form className="space-y-6">
          {/* Username */}
          <div>
            <label className="block text-lg text-center text-gray-700">
              Username
            </label>
            <input
              type="text"
              className="mt-1 block w-11/12 mx-auto content-center px-4 py-5 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Username"
            />
          </div>

          {/* Email */}
          <div>
            <label className="block text-lg text-gray-700 text-center">
              Email
            </label>
            <input
              type="email"
              className="mt-1 block w-11/12 mx-auto px-4 py-5 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Email"
            />
          </div>

          {/* Password */}
          <div>
            <label className="block text-lg text-gray-700 text-center">
              Password <span className="text-red-500">*</span>
            </label>
            <input
              type="password"
              className="mt-1 block w-11/12 mx-auto px-4 py-5 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Password"
            />
          </div>

          {/* Confirm Password */}
          <div>
            <label className="block text-lg text-gray-700 text-center">
              Confirm Password <span className="text-red-500">*</span>
            </label>
            <input
              type="password"
              className="mt-1 block w-11/12 mx-auto px-4 py-5 border border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
              placeholder="Confirm Password"
            />
          </div>

          {/* Submit Button */}
          <div className="text-center mt-8">
            <button
              type="submit"
              className="w-half py-3 px-6 text-lg bg-black text-white rounded-md hover:bg-lamaSky hover:text-gray-600"
            >
              Submit
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}