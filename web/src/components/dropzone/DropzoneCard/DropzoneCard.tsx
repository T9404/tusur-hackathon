'use client';

import React from 'react';
import {useDropzone} from 'react-dropzone';
import {Trash2} from 'lucide-react';

import {Button} from '@/components/ui';

import {ACCEPT_FILE_TYPES} from './constants/acceptFileTypes.ts';
import type {FileType} from './constants/types.ts';
import {useDropzoneCard} from './hooks/useDropzoneCard.ts';
import {DropzoneImage} from "@/components/ui/DropzoneImage.tsx";

interface DropzoneCardProps extends Omit<React.HTMLAttributes<HTMLDivElement>, 'onChange'> {
    value?: File | string;
    onChange: (props?: File) => void;
    type?: FileType;
}

export const DropzoneCard = ({value, onChange, type = 'image', ...props}: DropzoneCardProps) => {
    const {functions} = useDropzoneCard({onChange, type});

    const {getRootProps, getInputProps} = useDropzone({
        maxFiles: 1,
        accept: ACCEPT_FILE_TYPES[type],
        onDropRejected: functions.onError,
        onDropAccepted: (files: File[]) => onChange(files[0])
    });

    return (
        <div {...getRootProps()} {...props}>
            {value ? (
                <div className='relative h-full w-full'>
                    <img
                        className='h-full w-full rounded-lg border'
                        src={typeof value === 'string' ? value : URL.createObjectURL(value)}
                        alt='activity-image'
                    />
                    <Button
                        type='button'
                        variant='secondary'
                        className='absolute right-0 top-0 m-2 rounded-full px-3'
                        onClick={(event) => {
                            event.stopPropagation();
                            functions.deleteFile();
                        }}
                    >
                        <Trash2 className='h-4 w-4'/>
                    </Button>
                </div>
            ) : (
                <DropzoneImage {...getInputProps} />
            )}
        </div>
    );
};
